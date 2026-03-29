package com.focusguard.accessibility.detectors

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.util.AccessibilityUtils

class InstagramDetector : Detector {
    private val DEBUG_MODE = true
    private val TAG = "FocusGuard_IG"

    override fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean {
        if (DEBUG_MODE) {
            logNodes(rootNode, 0)
        }

        // EXCLUDE DMs: strictly look for DM Chat thread or Inbox (avoiding the generic "direct" button match on home feed)
        val isDM = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val id = node.viewIdResourceName ?: ""
            id == "com.instagram.android:id/direct_thread_list_container" ||
            id == "com.instagram.android:id/row_thread_composer_edittext" ||
            id == "com.instagram.android:id/message_content" ||
            id == "com.instagram.android:id/thread_title" ||
            id.contains("direct_message_list", ignoreCase = true)
        })
        if (isDM) return false

        // EXCLUDE Stories: strictly look for Story viewer, NOT home feed story tray
        val isStory = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val id = node.viewIdResourceName ?: ""
            id == "com.instagram.android:id/reel_viewer_root" ||
            id == "com.instagram.android:id/story_interactable_header" ||
            id == "com.instagram.android:id/reel_viewer_image_view"
        })
        if (isStory) return false

        // STRATEGY 4: Window Title / Context Check
        val classNameStr = event.className?.toString() ?: ""
        val isReelsWindow = classNameStr.contains("ClipsTabFragment", ignoreCase = true) ||
                            classNameStr.contains("ReelsTabFragment", ignoreCase = true) ||
                            classNameStr.contains("ClipsHomeFragment", ignoreCase = true) ||
                            classNameStr.contains("IgReelsFragment", ignoreCase = true) ||
                            classNameStr.contains("ReelViewerFragment", ignoreCase = true) ||
                            classNameStr.contains("ClipsViewerFragment", ignoreCase = true)
        
        if (isReelsWindow) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by STRATEGY 4/3: ClassName matched $classNameStr")
            return true
        }

        // STRATEGY 3: Top-level ClassName Check
        val topLevelMatch = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val nodeClassStr = node.className?.toString() ?: ""
            nodeClassStr.contains("ClipsTabFragment", ignoreCase = true) ||
            nodeClassStr.contains("ReelsTabFragment", ignoreCase = true) ||
            nodeClassStr.contains("ClipsHomeFragment", ignoreCase = true) ||
            nodeClassStr.contains("IgReelsFragment", ignoreCase = true)
        })
        if (topLevelMatch) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by STRATEGY 3: Node className matched Reels fragment")
            return true
        }

        // STRATEGY 1: ViewId check (Reels Tab in nav bar)
        // Note: Sometimes the parent tab container is selected instead of the button itself
        val isReelsTabFocused = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val id = node.viewIdResourceName ?: ""
            val matchesId = id == "com.instagram.android:id/clips_tab" ||
                            id == "com.instagram.android:id/reels_tab_button" ||
                            id == "com.instagram.android:id/clips_navigation_tab"
            val isNodeActive = node.isSelected || node.isFocused || node.parent?.isSelected == true
            matchesId && isNodeActive
        })
        if (isReelsTabFocused) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by STRATEGY 1: ViewId matched and selected/focused")
            return true
        }

        // Existing explicit IDs (kind of Strategy 1 fallback)
        val hasClipsVideoContainer = AccessibilityUtils.findNodeById(rootNode, "com.instagram.android:id/clips_video_container") != null
        val hasClipsViewPager = AccessibilityUtils.findNodeById(rootNode, "com.instagram.android:id/clips_viewer_view_pager") != null
        if (hasClipsVideoContainer || hasClipsViewPager) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by STRATEGY 1 (legacy container): Explicit clips container found")
            return true
        }

        // STRATEGY 2: ContentDescription check
        val isReelsContentSelected = AccessibilityUtils.containsNodeMatch(rootNode, { node ->
            val desc = node.contentDescription?.toString() ?: ""
            val matchesDesc = desc.contains("Reels", ignoreCase = true)
            
            // check if part of bottom navigation bar by doing a weak parent match
            var parent = node.parent
            var inNavBar = false
            for (i in 0 until 3) {
                if (parent == null) break
                val parentId = parent.viewIdResourceName ?: ""
                if (parentId.contains("navigation", ignoreCase = true) ||
                    parentId.contains("tab_bar", ignoreCase = true) ||
                    parentId.contains("bottom_bar", ignoreCase = true) ||
                    parentId == "com.instagram.android:id/tab_bar") {
                    inNavBar = true
                    break
                }
                parent = parent.parent
            }
            val isNodeActive = node.isSelected || node.isChecked || node.isFocused || node.parent?.isSelected == true
            matchesDesc && isNodeActive && inNavBar
        })
        if (isReelsContentSelected) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by STRATEGY 2: ContentDescription matched and selected/focused in nav bar")
            return true
        }

        // Explore Reels inline
        val hasExploreMedia = AccessibilityUtils.findNodeById(rootNode, "com.instagram.android:id/explore_media_container") != null
        val exploreClips = AccessibilityUtils.findNodeById(rootNode, "com.instagram.android:id/clips_video_container") != null
        if (hasExploreMedia && exploreClips) {
            if (DEBUG_MODE) Log.d(TAG, "Blocked by implicit strategy: Explore inline reels")
            return true
        }

        return false
    }

    private fun logNodes(node: AccessibilityNodeInfo?, depth: Int) {
        if (node == null || depth > 8) return
        if (!node.viewIdResourceName.isNullOrEmpty() || !node.contentDescription.isNullOrEmpty()) {
            Log.d(TAG, "Depth $depth -> id: ${node.viewIdResourceName}, desc: ${node.contentDescription}, selected: ${node.isSelected}, checked: ${node.isChecked}, focused: ${node.isFocused}")
        }
        for (i in 0 until node.childCount) {
            logNodes(node.getChild(i), depth + 1)
        }
    }
}


