package com.focusguard.domain.usecase

import com.focusguard.domain.model.BlockMode
import com.focusguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlockModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<BlockMode> = repository.blockMode
}
