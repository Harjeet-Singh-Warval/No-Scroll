package com.focusguard.accessibility.detectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.util.AccessibilityUtils

class TikTokDetector : Detector {
    override fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean {
        // Any fullscreen vertical video = block. TikTok is entirely short video basically.
        
        // Detect via className
        val isFeedActivity = event.className?.contains("com.ss.android.ugc.aweme.feed.FeedActivity") == true
        if (isFeedActivity) return true
        
        // Detect via viewId
        val hasVideoPlayer = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            node.viewIdResourceName?.contains("video_player") == true
        })
        if (hasVideoPlayer) return true
        
        return false
    }
}
