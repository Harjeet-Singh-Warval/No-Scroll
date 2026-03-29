package com.focusguard.ui.screens.settings

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkTheme: Boolean? = null,
    val blockAction: String = "CLOSE_VIDEO",
    val curiousLimit: Int = 3,
    val pinProtection: String? = null,
    val appEnabledStates: Map<String, Boolean> = emptyMap(),
    val isPremium: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isBatteryOptimized: Boolean = false,
    val isAutoStartEnabled: Boolean = true // Stub for future auto-start repo setting
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkTheme,
                settingsRepository.blockAction,
                settingsRepository.curiousLimit,
                settingsRepository.pinProtection
            ) { isDark, action, limit, pin ->
                SettingsUiState(
                    isDarkTheme = isDark,
                    blockAction = action,
                    curiousLimit = limit,
                    pinProtection = pin
                )
            }.combine(
                combine(
                    settingsRepository.isAppEnabled,
                    settingsRepository.isPremium,
                    settingsRepository.isAutoStartEnabled
                ) { apps, premium, autoStart -> Triple(apps, premium, autoStart) }
            ) { state, (apps, premium, autoStart) ->
                state.copy(
                    appEnabledStates = apps,
                    isPremium = premium,
                    isAutoStartEnabled = autoStart,
                    isBatteryOptimized = checkBatteryOptimized(),
                    notificationEnabled = _uiState.value.notificationEnabled
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun checkBatteryOptimized(): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun setTheme(isDark: Boolean?) {
        viewModelScope.launch { settingsRepository.setIsDarkTheme(isDark) }
    }

    fun setBlockAction(action: String) {
        viewModelScope.launch { settingsRepository.setBlockAction(action) }
    }

    fun setCuriousLimit(limit: Int) {
        viewModelScope.launch { settingsRepository.setCuriousLimit(limit) }
    }

    fun setPin(pin: String?) {
        viewModelScope.launch { settingsRepository.setPinProtection(pin) }
    }

    fun setAppEnabled(packageName: String, enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAppEnabled(packageName, enabled) }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationEnabled(enabled) }
    }

    fun setAutoStartEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setIsAutoStartEnabled(enabled) }
    }

    fun resetAllData() {
        viewModelScope.launch { settingsRepository.resetAllData() }
    }
}
