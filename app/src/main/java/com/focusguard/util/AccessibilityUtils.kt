package com.focusguard.util

import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityUtils {
    /**
     * Finds a node by viewId recursively, limited by depth to prevent battery drain.
     */
    fun findNodeById(node: AccessibilityNodeInfo?, id: String, maxDepth: Int = 8, currentDepth: Int = 0): AccessibilityNodeInfo? {
        if (node == null || currentDepth > maxDepth) return null
        if (node.viewIdResourceName == id) return node
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val found = findNodeById(child, id, maxDepth, currentDepth + 1)
            
            if (found != null) return found
        }
        return null
    }

    /**
     * Checks if any node in tree contains matching text in className, contentDescription or viewId.
     */
    fun containsNodeMatch(node: AccessibilityNodeInfo?, predicate: (AccessibilityNodeInfo) -> Boolean, maxDepth: Int = 8, currentDepth: Int = 0): Boolean {
        if (node == null || currentDepth > maxDepth) return false
        if (predicate(node)) return true
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val found = containsNodeMatch(child, predicate, maxDepth, currentDepth + 1)
            
            if (found) return true
        }
        return false
    }
}
