package com.focusguard.domain.repository

import com.focusguard.data.local.dao.DateCount
import com.focusguard.domain.model.BlockSession
import kotlinx.coroutines.flow.Flow

interface BlockSessionRepository {
    suspend fun insertSession(session: BlockSession)
    fun getSessionsForDate(dateKey: String): Flow<List<BlockSession>>
    fun getAllSessions(): Flow<List<BlockSession>>
    fun getSessionCountForDate(dateKey: String): Flow<Int>
    fun getWeeklyBlockCounts(): Flow<List<DateCount>>
}

