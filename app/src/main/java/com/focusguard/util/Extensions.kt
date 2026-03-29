package com.focusguard.util

import android.widget.Toast
import android.content.Context

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
