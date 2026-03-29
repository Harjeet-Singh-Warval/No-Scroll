package com.focusguard.accessibility.detectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.util.AccessibilityUtils

class FacebookDetector : Detector {
    override fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean {
        // EXCLUDE Messenger, Groups, Marketplace
        val isMessenger = event.className?.contains("Messenger") == true
        if (isMessenger) return false

        // Detect Reels by viewId
        val hasReelsContainer = AccessibilityUtils.findNodeById(rootNode, "com.facebook.katana:id/reels_video_container") != null
        if (hasReelsContainer) return true

        // Detect by contentDescription
        val hasReelDesc = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            node.contentDescription?.toString()?.contains("Reel", ignoreCase = true) == true
        })
        if (hasReelDesc) return true

        // Detect Watch fragment indicating shorts
        val isWatchFragment = event.className?.contains("WatchFragment") == true
        if (isWatchFragment) {
            // Need short video indicator, for simplicity block or check for specific ids
            // returning true as a proxy if we see short video indicators
            val hasShortIndicator = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
                node.contentDescription?.toString()?.contains("Short video", ignoreCase = true) == true
            })
            if (hasShortIndicator) return true
        }

        return false
    }
}
