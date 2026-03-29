package com.focusguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focusguard.domain.model.BlockSession

@Entity(tableName = "block_sessions")
data class BlockSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appPackage: String,
    val blockedAt: Long,
    val blockAction: String,
    val dateKey: String // yyyy-MM-dd
) {
    fun toDomainModel(): BlockSession {
        return BlockSession(
            id = id,
            appPackage = appPackage,
            blockedAt = blockedAt,
            blockAction = blockAction,
            dateKey = dateKey
        )
    }

    companion object {
        fun fromDomainModel(session: BlockSession): BlockSessionEntity {
            return BlockSessionEntity(
                id = session.id,
                appPackage = session.appPackage,
                blockedAt = session.blockedAt,
                blockAction = session.blockAction,
                dateKey = session.dateKey
            )
        }
    }
}
