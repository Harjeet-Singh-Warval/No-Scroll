package com.focusguard.accessibility.actions

import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
import android.content.Context

object BlockAction {
    fun execute(service: AccessibilityService, actionType: BlockActionType) {
        when (actionType) {
            BlockActionType.CLOSE_VIDEO -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            }
            BlockActionType.EXIT_APP -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            }
            BlockActionType.LOCK_SCREEN -> {
                val dpm = service.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                try {
                    dpm.lockNow()
                } catch (e: Exception) {
                    // Fallback to Home if admin not granted
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
                }
            }
        }
    }
}
