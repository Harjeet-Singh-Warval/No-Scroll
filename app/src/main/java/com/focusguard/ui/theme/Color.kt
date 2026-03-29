package com.focusguard.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// DARK THEME COLORS ("V4 Pixel-Perfect" UI Match)
// ==========================================
val DarkBackground = Color(0xFF121215)
val DarkSurface = Color(0xFF1C1C22)
val DarkSurfaceVariant = Color(0xFF23232A)
val DarkPrimary = Color(0xFF7B61FF)
val DarkSecondary = Color(0xFF54E087)
val DarkSuccess = Color(0xFF54E087)
val DarkTextPrimary = Color(0xFFF4F4F6)
val DarkTextSecondary = Color(0xFF8E8DA0)
val DarkBorder = Color(0xFF2C2C35)

// ==========================================
// LIGHT THEME COLORS
// ==========================================
val LightBackground = Color(0xFFF5F5FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDEDF5)
val LightPrimary = Color(0xFF5A52E0)
val LightSecondary = Color(0xFFE8334A)
val LightSuccess = Color(0xFF2DB866)
val LightTextPrimary = Color(0xFF0A0A0F)
val LightTextSecondary = Color(0xFF555570)
val LightBorder = Color(0xFFE0E0EC)

// Legacy colors kept for compatibility during migration, but we will mostly use MaterialTheme colors
val PrimaryPurple = DarkPrimary
val DangerCoral = DarkSecondary
val SuccessGreen = DarkSuccess
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
