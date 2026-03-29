package com.focusguard.domain.usecase

import com.focusguard.domain.model.BlockMode
import com.focusguard.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveBlockModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(mode: BlockMode) {
        repository.setBlockMode(mode)
    }
}
