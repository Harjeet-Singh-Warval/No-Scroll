package com.focusguard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// In a real app we'd load Inter from Google Fonts or local res/font. 
// For this scaffolding, we use default sans-serif to approximate Inter, but structure it identically.
val InterFamily = FontFamily.SansSerif

val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 13.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 13.sp
    ),
    // Caption maps to displaySmall for our custom usage
    displaySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 12.sp
    )
)
