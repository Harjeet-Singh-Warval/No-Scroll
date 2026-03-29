package com.focusguard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.focusguard.data.local.dao.BlockSessionDao
import com.focusguard.data.local.entity.BlockSessionEntity

@Database(entities = [BlockSessionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val blockSessionDao: BlockSessionDao
}
