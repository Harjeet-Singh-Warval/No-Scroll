package com.focusguard.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.focusguard.accessibility.actions.BlockAction
import com.focusguard.accessibility.actions.BlockActionType
import com.focusguard.accessibility.detectors.*
import com.focusguard.domain.model.AppTarget
import com.focusguard.domain.model.BlockMode
import com.focusguard.domain.repository.SettingsRepository
import com.focusguard.domain.usecase.LogBlockEventUseCase
import com.focusguard.service.FocusGuardForegroundService
import com.focusguard.util.PackageNames
import com.focusguard.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class FocusGuardAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var logBlockEventUseCase: LogBlockEventUseCase

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val detectors = mapOf(
        PackageNames.INSTAGRAM to InstagramDetector(),
        PackageNames.YOUTUBE to YouTubeDetector(),
        PackageNames.FACEBOOK to FacebookDetector(),
        PackageNames.SNAPCHAT to SnapchatDetector(),
        PackageNames.TIKTOK to TikTokDetector()
    )

    private val cooldownMap = mutableMapOf<String, Long>()
    private val COOLDOWN_DURATION = 2000L

    private var lastEventTime = 0L
    private val DEBOUNCE_DELAY = 300L

    override fun onServiceConnected() {
        super.onServiceConnected()
        try {
            startService(Intent(this, FocusGuardForegroundService::class.java))
        } catch (e: Exception) {
            // Failure starting service
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (!PackageNames.ALL_SUPPORTED.contains(packageName)) return

        val now = System.currentTimeMillis()
        if (now - lastEventTime < DEBOUNCE_DELAY) return
        lastEventTime = now

        val lastBlockTime = cooldownMap[packageName] ?: 0L
        if (now - lastBlockTime < COOLDOWN_DURATION) return

        val rootNode = rootInActiveWindow ?: return
        val detector = detectors[packageName] ?: return

        scope.launch {
            val isAppEnabled = settingsRepository.isAppEnabled.first()[packageName] ?: true
            if (!isAppEnabled) return@launch

            val isShorts = detector.detect(event, rootNode)
            if (isShorts) {
                handleBlock(packageName)
            }
        }
    }

    private suspend fun handleBlock(packageName: String) {
        val mode = settingsRepository.blockMode.first()
        val pausedUntil = settingsRepository.pausedUntilTimestamp.first()
        val now = System.currentTimeMillis()

        if (mode == BlockMode.PAUSED) {
            if (now < pausedUntil) {
                return
            } else {
                // Unpause automatically
                settingsRepository.setBlockMode(BlockMode.BLOCK_ALL)
            }
        }

        if (mode == BlockMode.CURIOUS) {
            val count = settingsRepository.curiousCount.first()
            val limit = settingsRepository.curiousLimit.first()
            if (count < limit) {
                settingsRepository.incrementCuriousCount()
                withContext(Dispatchers.Main) {
                    showToast("Curious Mode: ${count + 1}/$limit watched.")
                }
                cooldownMap[packageName] = now
                return
            }
        }

        val actionStr = settingsRepository.blockAction.first()
        val actionType = try {
            BlockActionType.valueOf(actionStr)
        } catch (e: Exception) {
            BlockActionType.CLOSE_VIDEO
        }

        withContext(Dispatchers.Main) {
            BlockAction.execute(this@FocusGuardAccessibilityService, actionType)
            showToast("Reels blocked by FocusGuard \ud83d\udee1\ufe0f")
        }

        withContext(Dispatchers.IO) {
            logBlockEventUseCase(packageName, actionType.name)
        }
        cooldownMap[packageName] = System.currentTimeMillis()
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
