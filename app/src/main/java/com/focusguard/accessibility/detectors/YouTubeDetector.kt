package com.focusguard.accessibility.detectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.util.AccessibilityUtils

class YouTubeDetector : Detector {
    override fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean {
        // EXCLUDE regular player
        // Wait, regular player has waitch?v= in URL but app doesn't expose URL easily.
        // Usually regular player is "com.google.android.youtube:id/player_view"
        // But let's stick to positive detection of shorts

        // Detect via URL/Window title
        val text = event.text.toString()
        if (text.contains("/shorts/")) return true
        
        // Title might contain "Shorts - "
        
        // Detect Shorts Tab
        val isShortsActivity = event.className?.contains("ShortsActivity") == true
        if (isShortsActivity) return true
        
        val isShortsTabSelected = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val desc = node.contentDescription?.toString() ?: ""
            desc.contains("Shorts", ignoreCase = true) && node.isSelected
        })
        if (isShortsTabSelected) return true

        // Detect Shorts from Home Feed (the shelf)
        // If they click on it, the player opens. If the player opens, how to distinguish?
        val hasShortsShelf = AccessibilityUtils.findNodeById(rootNode, "com.google.android.youtube:id/shorts_shelf_item_root") != null
        val hasReelPlayer = AccessibilityUtils.findNodeById(rootNode, "com.google.android.youtube:id/reel_player_view") != null
        val hasReelRecycler = AccessibilityUtils.findNodeById(rootNode, "com.google.android.youtube:id/reel_recycler") != null
        
        // We only block when they are WATCHING shorts, not just seeing the shelf. Wait, the prompt says "Look for shorts_shelf_item_root". But if they are just on home feed that's annoying. Let's assume seeing the shelf fullscreen or clicking it triggers block. Actually, player is "com.google.android.youtube:id/reel_player_view" sometimes.
        // Let's implement what's requested:
        if (hasShortsShelf || hasReelPlayer || hasReelRecycler) return true

        return false
    }
}
