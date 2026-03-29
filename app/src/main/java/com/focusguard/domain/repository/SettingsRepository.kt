package com.focusguard.domain.repository

import com.focusguard.domain.model.BlockMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val blockMode: Flow<BlockMode>
    val pausedUntilTimestamp: Flow<Long>
    val curiousCount: Flow<Int>
    val curiousLimit: Flow<Int>
    val blockAction: Flow<String> // enum string of BlockActionType
    val pinProtection: Flow<String?> // null if not set
    val isAppEnabled: Flow<Map<String, Boolean>> // packageName -> enabled
    val isPremium: Flow<Boolean>
    val notificationEnabled: Flow<Boolean>
    val isAutoStartEnabled: Flow<Boolean>
    val isDarkTheme: Flow<Boolean?> // null means follow system default

    suspend fun setBlockMode(mode: BlockMode)
    suspend fun setPausedUntil(timestamp: Long)
    suspend fun setCuriousCount(count: Int)
    suspend fun incrementCuriousCount()
    suspend fun setCuriousLimit(limit: Int)
    suspend fun setBlockAction(action: String)
    suspend fun setPinProtection(pin: String?)
    suspend fun setAppEnabled(packageName: String, enabled: Boolean)
    suspend fun setIsPremium(premium: Boolean)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setIsAutoStartEnabled(enabled: Boolean)
    suspend fun setIsDarkTheme(isDark: Boolean?)
    suspend fun resetAllData()
}
