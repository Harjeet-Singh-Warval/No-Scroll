package com.focusguard.domain.usecase

import com.focusguard.domain.repository.BlockSessionRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val repository: BlockSessionRepository
) {
    operator fun invoke(): Flow<Int> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return repository.getSessionCountForDate(today)
    }
}
