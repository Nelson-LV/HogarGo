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

    @Query("SELECT * FROM members WHERE householdId = :householdId ORDER BY createdAt ASC")
    fun observeByHousehold(householdId: String): Flow<List<MemberEntity>>

    /** Returns -1 when that name already exists in the household. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: MemberEntity): Long
}
