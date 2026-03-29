package com.focusguard.domain.model

enum class AppTarget(val packageName: String, val appName: String) {
    INSTAGRAM("com.instagram.android", "Instagram"),
    YOUTUBE("com.google.android.youtube", "YouTube"),
    FACEBOOK("com.facebook.katana", "Facebook"),
    SNAPCHAT("com.snapchat.android", "Snapchat"),
    TIKTOK("com.zhiliaoapp.musically", "TikTok");

    companion object {
        fun fromPackageName(packageName: String): AppTarget? {
            return entries.find { it.packageName == packageName }
        }
    }
}
