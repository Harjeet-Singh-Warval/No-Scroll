package com.focusguard.accessibility.detectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.util.AccessibilityUtils

class SnapchatDetector : Detector {
    override fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean {
        // Detect Spotlight
        val hasSpotlight = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            node.viewIdResourceName?.contains("spotlight", ignoreCase = true) == true ||
            node.contentDescription?.toString()?.contains("Spotlight", ignoreCase = true) == true
        })
        
        if (hasSpotlight) return true
        
        return false
    }
}
