package com.focusguard.data.repository

import com.focusguard.data.datastore.UserPreferencesDataStore
import com.focusguard.domain.model.AppTarget
import com.focusguard.domain.model.BlockMode
import com.focusguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : SettingsRepository {

    override val blockMode: Flow<BlockMode>
        get() = dataStore.preferencesFlow.map { prefs ->
            val modeStr = prefs[UserPreferencesDataStore.BLOCK_MODE] ?: BlockMode.BLOCK_ALL.name
            try { BlockMode.valueOf(modeStr) } catch (e: Exception) { BlockMode.BLOCK_ALL }
        }

    override val pausedUntilTimestamp: Flow<Long>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.PAUSED_UNTIL] ?: 0L
        }

    override val curiousCount: Flow<Int>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.CURIOUS_COUNT] ?: 0
        }

    override val curiousLimit: Flow<Int>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.CURIOUS_LIMIT] ?: 3
        }

    override val blockAction: Flow<String>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.BLOCK_ACTION] ?: "CLOSE_VIDEO"
        }

    override val pinProtection: Flow<String?>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.PIN_PROTECTION]
        }

    override val isAppEnabled: Flow<Map<String, Boolean>>
        get() = dataStore.preferencesFlow.map { prefs ->
            val map = mutableMapOf<String, Boolean>()
            AppTarget.entries.forEach { target ->
                val key = UserPreferencesDataStore.getAppEnabledKey(target.packageName)
                map[target.packageName] = prefs[key] ?: true
            }
            map
        }

    override val isPremium: Flow<Boolean>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.IS_PREMIUM] ?: false
        }

    override val isAutoStartEnabled: Flow<Boolean>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.IS_AUTO_START_ENABLED] ?: true
        }

    override val notificationEnabled: Flow<Boolean>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.NOTIFICATION_ENABLED] ?: true
        }

    override val isDarkTheme: Flow<Boolean?>
        get() = dataStore.preferencesFlow.map { prefs ->
            prefs[UserPreferencesDataStore.IS_DARK_THEME]
        }

    override suspend fun setBlockMode(mode: BlockMode) {
        dataStore.savePreference(UserPreferencesDataStore.BLOCK_MODE, mode.name)
    }

    override suspend fun setPausedUntil(timestamp: Long) {
        dataStore.savePreference(UserPreferencesDataStore.PAUSED_UNTIL, timestamp)
    }

    override suspend fun setCuriousCount(count: Int) {
        dataStore.savePreference(UserPreferencesDataStore.CURIOUS_COUNT, count)
    }

    override suspend fun incrementCuriousCount() {
        dataStore.incrementCuriousCount()
    }

    override suspend fun setCuriousLimit(limit: Int) {
        dataStore.savePreference(UserPreferencesDataStore.CURIOUS_LIMIT, limit)
    }

    override suspend fun setBlockAction(action: String) {
        dataStore.savePreference(UserPreferencesDataStore.BLOCK_ACTION, action)
    }

    override suspend fun setPinProtection(pin: String?) {
        if (pin.isNullOrEmpty()) {
            dataStore.removePreference(UserPreferencesDataStore.PIN_PROTECTION)
        } else {
            dataStore.savePreference(UserPreferencesDataStore.PIN_PROTECTION, pin)
        }
    }

    override suspend fun setAppEnabled(packageName: String, enabled: Boolean) {
        dataStore.savePreference(UserPreferencesDataStore.getAppEnabledKey(packageName), enabled)
    }

    override suspend fun setIsPremium(premium: Boolean) {
        dataStore.savePreference(UserPreferencesDataStore.IS_PREMIUM, premium)
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.savePreference(UserPreferencesDataStore.NOTIFICATION_ENABLED, enabled)
    }

    override suspend fun setIsDarkTheme(isDark: Boolean?) {
        if (isDark == null) {
            dataStore.removePreference(UserPreferencesDataStore.IS_DARK_THEME)
        } else {
            dataStore.savePreference(UserPreferencesDataStore.IS_DARK_THEME, isDark)
        }
    }

    override suspend fun setIsAutoStartEnabled(enabled: Boolean) {
        dataStore.savePreference(UserPreferencesDataStore.IS_AUTO_START_ENABLED, enabled)
    }

    override suspend fun resetAllData() {
        dataStore.reset()
    }
}

