package com.focusguard.data.repository

import com.focusguard.data.local.dao.BlockSessionDao
import com.focusguard.data.local.dao.DateCount
import com.focusguard.data.local.entity.BlockSessionEntity
import com.focusguard.domain.model.BlockSession
import com.focusguard.domain.repository.BlockSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockSessionRepositoryImpl @Inject constructor(
    private val dao: BlockSessionDao
) : BlockSessionRepository {
    override suspend fun insertSession(session: BlockSession) {
        dao.insertSession(BlockSessionEntity.fromDomainModel(session))
    }

    override fun getSessionsForDate(dateKey: String): Flow<List<BlockSession>> {
        return dao.getSessionsForDate(dateKey).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllSessions(): Flow<List<BlockSession>> {
        return dao.getAllSessions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSessionCountForDate(dateKey: String): Flow<Int> {
        return dao.getSessionCountForDate(dateKey)
    }

    override fun getWeeklyBlockCounts(): Flow<List<DateCount>> {
        return dao.getWeeklyBlockCounts()
    }
}

