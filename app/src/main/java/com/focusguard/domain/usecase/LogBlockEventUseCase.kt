package com.focusguard.domain.usecase

import com.focusguard.domain.model.BlockSession
import com.focusguard.domain.repository.BlockSessionRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class LogBlockEventUseCase @Inject constructor(
    private val repository: BlockSessionRepository
) {
    suspend operator fun invoke(appPackage: String, blockAction: String) {
        val now = System.currentTimeMillis()
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        
        repository.insertSession(
            BlockSession(
                appPackage = appPackage,
                blockedAt = now,
                blockAction = blockAction,
                dateKey = dateKey
            )
        )
    }
}
