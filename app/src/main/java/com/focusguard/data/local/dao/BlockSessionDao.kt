package com.focusguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusguard.data.local.entity.BlockSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BlockSessionEntity)

    @Query("SELECT * FROM block_sessions WHERE dateKey = :dateKey ORDER BY blockedAt DESC")
    fun getSessionsForDate(dateKey: String): Flow<List<BlockSessionEntity>>

    @Query("SELECT * FROM block_sessions ORDER BY blockedAt DESC")
    fun getAllSessions(): Flow<List<BlockSessionEntity>>

    @Query("SELECT COUNT(*) FROM block_sessions WHERE dateKey = :dateKey")
    fun getSessionCountForDate(dateKey: String): Flow<Int>

    @Query("SELECT * FROM block_sessions WHERE dateKey >= :startDate AND dateKey <= :endDate ORDER BY blockedAt DESC")
    fun getSessionsForDateRange(startDate: String, endDate: String): Flow<List<BlockSessionEntity>>

    @Query("SELECT dateKey, COUNT(*) as cnt FROM block_sessions GROUP BY dateKey ORDER BY dateKey DESC LIMIT 7")
    fun getWeeklyBlockCounts(): Flow<List<DateCount>>
}

data class DateCount(val dateKey: String, val cnt: Int)
