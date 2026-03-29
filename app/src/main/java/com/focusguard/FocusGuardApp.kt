package com.focusguard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusGuardApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
