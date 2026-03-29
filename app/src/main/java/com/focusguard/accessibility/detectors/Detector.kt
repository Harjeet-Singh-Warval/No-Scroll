package com.focusguard.accessibility.detectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

interface Detector {
    /**
     * @return true if short-form video content is detected on the screen
     */
    fun detect(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo): Boolean
}
