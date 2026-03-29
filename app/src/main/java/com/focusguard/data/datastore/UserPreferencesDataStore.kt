package com.focusguard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val preferencesFlow: Flow<Preferences> = dataStore.data

    suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun incrementCuriousCount() {
        dataStore.edit { preferences ->
            val current = preferences[CURIOUS_COUNT] ?: 0
            preferences[CURIOUS_COUNT] = current + 1
        }
    }

    companion object {
        val BLOCK_MODE = stringPreferencesKey("block_mode")
        val PAUSED_UNTIL = longPreferencesKey("paused_until")
        val CURIOUS_COUNT = intPreferencesKey("curious_count")
        val CURIOUS_LIMIT = intPreferencesKey("curious_limit")
        val BLOCK_ACTION = stringPreferencesKey("block_action")
        val PIN_PROTECTION = stringPreferencesKey("pin_protection")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val IS_AUTO_START_ENABLED = booleanPreferencesKey("is_auto_start_enabled")
        
        fun getAppEnabledKey(packageName: String) = booleanPreferencesKey("app_enabled_$packageName")
    }
}
