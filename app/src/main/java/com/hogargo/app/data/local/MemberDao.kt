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

    @Query("DELETE FROM members WHERE householdId = :householdId AND id = :id AND isAdmin = 0")
    suspend fun deleteNonAdmin(householdId: String, id: String)

    /** Returns -1 when that name already exists in the household. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: MemberEntity): Long
}
