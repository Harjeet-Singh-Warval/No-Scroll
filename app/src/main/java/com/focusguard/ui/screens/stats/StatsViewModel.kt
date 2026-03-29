package com.focusguard.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.domain.repository.BlockSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class StatsUiState(
    val weeklyData: List<Pair<String, Int>> = emptyList(), // DayName to Count
    val totalBlockedAllTime: Int = 0,
    val bestDayCount: Int = 0,
    val bestDayName: String = "-",
    val longestStreak: Int = 0,
    val appBreakdown: List<Pair<String, Int>> = emptyList(),
    val totalTimeSavedMinutes: Int = 0,
    val todayIndex: Int = -1
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val blockSessionRepository: BlockSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            // In a real implementation we would observe a flow from the DB.
            // For now, let's observe weekly stats and mock the historical stats if missing.
            blockSessionRepository.getWeeklyBlockCounts().collect { dbData ->
                // Map dbData to the requirements or use placeholder
                val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                val today = sdf.format(Date())
                
                // MOCK DATA for visualization showcase matching the spec
                val mockWeekly = listOf(
                    "Mon" to 12, "Tue" to 25, "Wed" to 8, "Thu" to 42,
                    "Fri" to 15, "Sat" to 30, "Sun" to 10
                )
                
                val mockApps = listOf(
                    "Instagram" to 65,
                    "YouTube" to 35,
                    "TikTok" to 20,
                    "Facebook" to 12,
                    "Snapchat" to 5
                )

                val sum = mockWeekly.sumOf { it.second }
                val timeSaved = (sum * 35) / 60 

                _uiState.update { 
                    it.copy(
                        weeklyData = mockWeekly,
                        totalBlockedAllTime = 1450,
                        bestDayCount = 85,
                        bestDayName = "Friday",
                        longestStreak = 12,
                        appBreakdown = mockApps,
                        totalTimeSavedMinutes = timeSaved,
                        todayIndex = mockWeekly.indexOfFirst { pair -> pair.first == today }.takeIf { idx -> idx >= 0 } ?: 3
                    )
                }
            }
        }
    }
}
