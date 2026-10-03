package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): MemberEntity?

    @Query("SELECT * FROM members WHERE householdId = :householdId AND nameKey = :nameKey LIMIT 1")
    suspend fun findByName(householdId: String, nameKey: String): MemberEntity?

    /** Accepted members only. */
    @Query("SELECT * FROM members WHERE householdId = :householdId AND isApproved = 1 ORDER BY createdAt ASC")
    fun observeByHousehold(householdId: String): Flow<List<MemberEntity>>

    /** People who asked to join and are waiting for the admin. */
    @Query("SELECT * FROM members WHERE householdId = :householdId AND isApproved = 0 ORDER BY createdAt ASC")
    fun observePending(householdId: String): Flow<List<MemberEntity>>

    @Query("UPDATE members SET isApproved = 1 WHERE householdId = :householdId AND id = :id")
    suspend fun approve(householdId: String, id: String)

    /** Households this recovery user still belongs to (accepted memberships only). */
    @Query("SELECT * FROM members WHERE recoveryKey = :recoveryKey AND isApproved = 1")
    suspend fun findApprovedByRecoveryKey(recoveryKey: String): List<MemberEntity>

    @Query("SELECT * FROM members WHERE householdId = :householdId AND recoveryKey = :recoveryKey LIMIT 1")
    suspend fun findByRecoveryKey(householdId: String, recoveryKey: String): MemberEntity?

    @Query("UPDATE members SET name = :name, nameKey = :nameKey WHERE id = :id")
    suspend fun updateName(id: String, name: String, nameKey: String)

    @Query("UPDATE members SET recoveryKey = :recoveryKey WHERE id = :id")
    suspend fun setRecoveryKey(id: String, recoveryKey: String)

    /** Oldest accepted member other than [excludedId]: the one who inherits the admin role. */
    @Query("SELECT * FROM members WHERE householdId = :householdId AND isApproved = 1 AND id != :excludedId ORDER BY createdAt ASC LIMIT 1")
    suspend fun firstApprovedExcept(householdId: String, excludedId: String): MemberEntity?

    @Query("UPDATE members SET isAdmin = 1 WHERE householdId = :householdId AND id = :id")
    suspend fun makeAdmin(householdId: String, id: String)

    @Query("DELETE FROM members WHERE householdId = :householdId AND id = :id")
    suspend fun deleteById(householdId: String, id: String)

    @Query("DELETE FROM members WHERE householdId = :householdId")
    suspend fun deleteAllOfHousehold(householdId: String)

    @Query("DELETE FROM members WHERE householdId = :householdId AND id = :id AND isAdmin = 0")
    suspend fun deleteNonAdmin(householdId: String, id: String)

    /** Returns -1 when that name already exists in the household. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: MemberEntity): Long
}
