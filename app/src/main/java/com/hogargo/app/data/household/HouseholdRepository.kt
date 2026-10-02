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
<<<<<<< Updated upstream
=======
    PENDING_APPROVAL,
>>>>>>> Stashed changes
}

sealed interface AuthResult {
    data class Success(val session: ActiveSession) : AuthResult
    data class Failure(val error: AuthError) : AuthResult
<<<<<<< Updated upstream
=======

    /** The request was sent; the admin still has to accept it before the person can get in. */
    data object Pending : AuthResult
>>>>>>> Stashed changes
}

class HouseholdRepository(
    private val database: HogarGoDatabase,
    private val sessionStore: SessionStore,
) {
    private val householdDao = database.householdDao()
    private val memberDao = database.memberDao()

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
    suspend fun createHousehold(userName: String, householdName: String, code: String): AuthResult {
        val name = userName.trim()
        val homeName = householdName.trim()
        if (name.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_NAME)
        if (homeName.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_HOUSEHOLD_NAME)
        val normalized = HouseholdCode.normalize(code)
        if (normalized.length != HouseholdCode.LENGTH) return AuthResult.Failure(AuthError.INVALID_CODE)

        val now = System.currentTimeMillis()
        val household = HouseholdEntity(UUID.randomUUID().toString(), homeName, normalized, now)
        val member = newMember(household.id, name, isAdmin = true, now = now)

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

<<<<<<< Updated upstream
    /** Adds [userName] to the household that owns [rawCode] (and only that one) and signs them in. */
=======
    /** Sends a join request to the household that owns [rawCode]; the admin must accept it. */
>>>>>>> Stashed changes
    suspend fun joinHousehold(userName: String, rawCode: String): AuthResult {
        val name = userName.trim()
        if (name.isEmpty()) return AuthResult.Failure(AuthError.EMPTY_NAME)
        val code = HouseholdCode.normalize(rawCode)
        if (code.length != HouseholdCode.LENGTH) return AuthResult.Failure(AuthError.INVALID_CODE)

        val household = householdDao.findByCode(code) ?: return AuthResult.Failure(AuthError.CODE_NOT_FOUND)
<<<<<<< Updated upstream
        val member = newMember(household.id, name, isAdmin = false, now = System.currentTimeMillis())
        if (memberDao.insert(member) == -1L) return AuthResult.Failure(AuthError.NAME_TAKEN)
        return AuthResult.Success(open(member, household))
=======
        val member = newMember(household.id, name, isAdmin = false, now = System.currentTimeMillis(), approved = false)
        if (memberDao.insert(member) == -1L) return AuthResult.Failure(AuthError.NAME_TAKEN)
        return AuthResult.Pending
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
        if (!member.isApproved) return AuthResult.Failure(AuthError.PENDING_APPROVAL)
>>>>>>> Stashed changes
        return AuthResult.Success(open(member, household))
    }

    /** The person that was signed in the last time the app ran, if they still exist. */
    suspend fun restoreSession(): ActiveSession? {
        val memberId = sessionStore.memberId ?: return null
        val member = memberDao.findById(memberId)
        val household = member?.let { householdDao.findById(it.householdId) }
<<<<<<< Updated upstream
        if (member == null || household == null) {
=======
        if (member == null || household == null || !member.isApproved) {
>>>>>>> Stashed changes
            sessionStore.clear()
            return null
        }
        return ActiveSession(member, household)
    }

    fun signOut() = sessionStore.clear()

<<<<<<< Updated upstream
    private fun newMember(householdId: String, name: String, isAdmin: Boolean, now: Long) = MemberEntity(
=======
    private fun newMember(householdId: String, name: String, isAdmin: Boolean, now: Long, approved: Boolean = true) = MemberEntity(
>>>>>>> Stashed changes
        id = UUID.randomUUID().toString(),
        householdId = householdId,
        name = name,
        nameKey = name.lowercase(),
        isAdmin = isAdmin,
<<<<<<< Updated upstream
=======
        isApproved = approved,
>>>>>>> Stashed changes
        createdAt = now,
    )

    private fun open(member: MemberEntity, household: HouseholdEntity): ActiveSession {
        sessionStore.save(member.id, household.id)
        return ActiveSession(member, household)
    }
}
