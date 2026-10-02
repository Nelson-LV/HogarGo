package com.hogargo.app.data.household

import androidx.room.withTransaction
import com.hogargo.app.data.local.HogarGoDatabase
import com.hogargo.app.data.local.HouseholdEntity
import com.hogargo.app.data.local.MemberEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class ActiveSession(val member: MemberEntity, val household: HouseholdEntity)

enum class AuthError {
    EMPTY_NAME,
    EMPTY_HOUSEHOLD_NAME,
    INVALID_CODE,
    CODE_NOT_FOUND,
    NAME_TAKEN,
    MEMBER_NOT_FOUND,
    CODE_TAKEN,
    PENDING_APPROVAL,
    RECOVERY_USER_INVALID,
    RECOVERY_USER_SAME_AS_NAME,
}

sealed interface AuthResult {
    data class Success(val session: ActiveSession) : AuthResult
    data class Failure(val error: AuthError) : AuthResult

    /** The request was sent; the admin still has to accept it before the person can get in. */
    data object Pending : AuthResult
}

/** A household the recovery user still belongs to, with its invitation code. */
data class RecoveredHousehold(val householdName: String, val code: String, val isAdmin: Boolean)

class HouseholdRepository(
    private val database: HogarGoDatabase,
    private val sessionStore: SessionStore,
) {
    private val householdDao = database.householdDao()
    private val memberDao = database.memberDao()
    private val taskDao = database.taskDao()

    fun observeMembers(householdId: String): Flow<List<MemberEntity>> =
        memberDao.observeByHousehold(householdId)

    /** A code that no household is using yet. */
    suspend fun generateUniqueCode(): String {
        repeat(30) {
            val candidate = HouseholdCode.generate()
            if (householdDao.findByCode(candidate) == null) return candidate
        }
        return HouseholdCode.generate()
    }

    /** Creates a brand new household with [userName] as its admin and signs them in. */
    suspend fun createHousehold(userName: String, householdName: String, code: String, recoveryUser: String): AuthResult {
        val name = userName.trim()
        val homeName = householdName.trim()
        if (name.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_NAME)
        if (homeName.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_HOUSEHOLD_NAME)
        validateRecoveryUser(name, recoveryUser)?.let { return AuthResult.Failure(it) }
        val normalized = HouseholdCode.normalize(code)
        if (normalized.length != HouseholdCode.LENGTH) return AuthResult.Failure(AuthError.INVALID_CODE)

        val now = System.currentTimeMillis()
        val household = HouseholdEntity(UUID.randomUUID().toString(), homeName, normalized, now)
        val member = newMember(household.id, name, isAdmin = true, now = now, recoveryUser = recoveryUser)

        val created = database.withTransaction {
            if (householdDao.insert(household) == -1L) {
                false
            } else {
                memberDao.insert(member)
                true
            }
        }
        if (!created) return AuthResult.Failure(AuthError.CODE_TAKEN)
        return AuthResult.Success(open(member, household))
    }

    /** Sends a join request to the household that owns [rawCode]; the admin must accept it. */
    suspend fun joinHousehold(userName: String, rawCode: String, recoveryUser: String): AuthResult {
        val name = userName.trim()
        if (name.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_NAME)
        validateRecoveryUser(name, recoveryUser)?.let { return AuthResult.Failure(it) }
        val code = HouseholdCode.normalize(rawCode)
        if (code.length != HouseholdCode.LENGTH) return AuthResult.Failure(AuthError.INVALID_CODE)

        val household = householdDao.findByCode(code) ?: return AuthResult.Failure(AuthError.CODE_NOT_FOUND)
        val member = newMember(household.id, name, isAdmin = false, now = System.currentTimeMillis(), approved = false, recoveryUser = recoveryUser)
        if (memberDao.insert(member) == -1L) return AuthResult.Failure(AuthError.NAME_TAKEN)
        return AuthResult.Pending
    }

    /** Signs back in as an existing member of the household that owns [rawCode]. */
    suspend fun signIn(userName: String, rawCode: String): AuthResult {
        val name = userName.trim()
        if (name.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_NAME)
        val code = HouseholdCode.normalize(rawCode)
        if (code.length != HouseholdCode.LENGTH) return AuthResult.Failure(AuthError.INVALID_CODE)

        val household = householdDao.findByCode(code) ?: return AuthResult.Failure(AuthError.CODE_NOT_FOUND)
        val member = memberDao.findByName(household.id, name.lowercase())
            ?: return AuthResult.Failure(AuthError.MEMBER_NOT_FOUND)
        if (!member.isApproved) return AuthResult.Failure(AuthError.PENDING_APPROVAL)
        return AuthResult.Success(open(member, household))
    }

    /** The person that was signed in the last time the app ran, if they still exist. */
    suspend fun restoreSession(): ActiveSession? {
        val memberId = sessionStore.memberId ?: return null
        val member = memberDao.findById(memberId)
        val household = member?.let { householdDao.findById(it.householdId) }
        if (member == null || household == null || !member.isApproved) {
            sessionStore.clear()
            return null
        }
        return ActiveSession(member, household)
    }

    fun signOut() = sessionStore.clear()

    // ---- Recovery user

    private fun validateRecoveryUser(name: String, rawUser: String): AuthError? {
        val user = RecoveryUser.normalize(rawUser)
        return when {
            user.length < RecoveryUser.MIN_LENGTH -> AuthError.RECOVERY_USER_INVALID
            user == name.trim().lowercase() -> AuthError.RECOVERY_USER_SAME_AS_NAME
            else -> null
        }
    }

    /** Sets or changes the recovery user of an existing member. Returns the error, or null on success. */
    suspend fun setRecoveryUser(memberId: String, rawUser: String): AuthError? {
        val member = memberDao.findById(memberId) ?: return AuthError.MEMBER_NOT_FOUND
        validateRecoveryUser(member.name, rawUser)?.let { return it }
        memberDao.setRecoveryKey(memberId, RecoveryUser.hash(rawUser))
        return null
    }

    /**
     * The codes of every household [rawUser] still belongs to. Memberships that were removed
     * (kicked out or left) or that are still pending don't show up, so their code disappears too.
     */
    suspend fun recoverCodes(rawUser: String): List<RecoveredHousehold> {
        if (RecoveryUser.normalize(rawUser).isEmpty()) return emptyList()
        return memberDao.findApprovedByRecoveryKey(RecoveryUser.hash(rawUser)).mapNotNull { member ->
            householdDao.findById(member.householdId)?.let {
                RecoveredHousehold(it.name, it.code, member.isAdmin)
            }
        }
    }

    // ---- Leaving

    /**
     * [memberId] leaves their household. If they were the admin the role goes to the oldest
     * remaining member; if they were the only person, the whole household is deleted.
     */
    suspend fun leaveHousehold(memberId: String, householdId: String) {
        sessionStore.clear()
        database.withTransaction { leaveInTransaction(memberId, householdId) }
    }

    private suspend fun leaveInTransaction(memberId: String, householdId: String) {
        val me = memberDao.findById(memberId) ?: return
        if (me.householdId != householdId) return

        if (me.isAdmin) {
            val successor = memberDao.firstApprovedExcept(householdId, memberId)
            if (successor == null) {
                deleteHouseholdCompletely(householdId)
                return
            }
            memberDao.makeAdmin(householdId, successor.id)
        }
        taskDao.clearAssignee(householdId, memberId)
        memberDao.deleteById(householdId, memberId)
    }

    private suspend fun deleteHouseholdCompletely(householdId: String) {
        database.expenseDao().deleteAllOfHousehold(householdId)
        database.savingsGoalDao().deleteAllOfHousehold(householdId)
        database.petStateDao().deleteAllOfHousehold(householdId)
        database.wardrobeItemDao().deleteAllOfHousehold(householdId)
        database.billDao().deleteAllOfHousehold(householdId)
        database.eventDao().deleteAllOfHousehold(householdId)
        taskDao.deleteAllOfHousehold(householdId)
        memberDao.deleteAllOfHousehold(householdId)
        householdDao.deleteById(householdId)
    }

    private fun newMember(householdId: String, name: String, isAdmin: Boolean, now: Long, approved: Boolean = true, recoveryUser: String = "") = MemberEntity(
        id = UUID.randomUUID().toString(),
        householdId = householdId,
        name = name,
        nameKey = name.lowercase(),
        isAdmin = isAdmin,
        isApproved = approved,
        recoveryKey = if (recoveryUser.isBlank()) "" else RecoveryUser.hash(recoveryUser),
        createdAt = now,
    )

    private fun open(member: MemberEntity, household: HouseholdEntity): ActiveSession {
        sessionStore.save(member.id, household.id)
        return ActiveSession(member, household)
    }
}
