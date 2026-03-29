package com.focusguard.ui.screens.home

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.domain.model.AppTarget
import com.focusguard.domain.model.BlockMode
import com.focusguard.domain.repository.SettingsRepository
import com.focusguard.domain.usecase.GetBlockModeUseCase
import com.focusguard.domain.usecase.GetTodayStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isServiceEnabled: Boolean = false,
    val blockMode: BlockMode = BlockMode.BLOCK_ALL,
    val todayBlockCount: Int = 0,
    val appEnabledStates: Map<String, Boolean> = emptyMap(),
    val isPremium: Boolean = false,
    val curiousRemaining: Int = 3,
    val isDarkTheme: Boolean? = null,
    val streakDays: Int = 0 
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBlockModeUseCase: GetBlockModeUseCase,
    private val getTodayStatsUseCase: GetTodayStatsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAccessibilityService()
        
        viewModelScope.launch {
            val flows1 = combine(
                getBlockModeUseCase(),
                getTodayStatsUseCase(),
                settingsRepository.isAppEnabled
            ) { mode, count, apps -> Triple(mode, count, apps) }
            
            val flows2 = combine(
                settingsRepository.isPremium,
                settingsRepository.curiousLimit,
                settingsRepository.isDarkTheme
            ) { premium, limit, dark -> Triple(premium, limit, dark) }

            combine(flows1, flows2) { f1, f2 ->
                val (mode, count, apps) = f1
                val (premium, limit, darkTheme) = f2
                HomeUiState(
                    blockMode = mode,
                    todayBlockCount = count,
                    appEnabledStates = apps,
                    isPremium = premium,
                    curiousRemaining = limit,
                    isDarkTheme = darkTheme,
                    isServiceEnabled = _uiState.value.isServiceEnabled,
                    streakDays = 0 // Placeholder
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun checkAccessibilityService() {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        val isEnabled = enabledServices.any { it.resolveInfo.serviceInfo.packageName == context.packageName }
        _uiState.update { it.copy(isServiceEnabled = isEnabled) }
    }

    fun toggleApp(target: AppTarget, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAppEnabled(target.packageName, enabled)
        }
    }

    fun setBlockMode(mode: BlockMode) {
        viewModelScope.launch {
            settingsRepository.setBlockMode(mode)
            if (mode != BlockMode.PAUSED) {
                settingsRepository.setPausedUntil(0L)
            }
        }
    }

    fun takeBreak(minutes: Int) {
        viewModelScope.launch {
            val pauseUntil = System.currentTimeMillis() + (minutes * 60 * 1000L)
            settingsRepository.setPausedUntil(pauseUntil)
            settingsRepository.setBlockMode(BlockMode.PAUSED)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val isDark = _uiState.value.isDarkTheme
            // If null (system), we force light or dark. Let's toggle between true/false
            val nextTheme = if (isDark == true) false else true
            settingsRepository.setIsDarkTheme(nextTheme)
        }
    }
}
