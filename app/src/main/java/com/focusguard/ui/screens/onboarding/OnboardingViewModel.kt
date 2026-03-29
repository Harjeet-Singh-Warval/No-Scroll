package com.focusguard.ui.screens.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.domain.repository.SettingsRepository
import com.focusguard.util.AccessibilityCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isAccessibilityEnabled: Boolean = false,
    val appEnabledStates: Map<String, Boolean> = emptyMap()
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.isAppEnabled.collect { apps ->
                _uiState.update { it.copy(appEnabledStates = apps) }
            }
        }
    }

    fun checkAccessibility() {
        val enabled = AccessibilityCheck.isAccessibilityServiceEnabled(context, com.focusguard.accessibility.FocusGuardAccessibilityService::class.java)
        _uiState.update { it.copy(isAccessibilityEnabled = enabled) }
    }

    fun toggleApp(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAppEnabled(packageName, enabled)
        }
    }
}
