package com.focusguard.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.R
import com.focusguard.domain.model.AppTarget

@Composable
fun AppToggleCard(
    appTarget: AppTarget,
    isEnabled: Boolean,
    isPremiumLocked: Boolean = false,
    onToggle: (Boolean) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isEnabled) 1.0f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    val iconRes = when (appTarget) {
        AppTarget.INSTAGRAM -> R.drawable.ic_instagram_v4
        AppTarget.YOUTUBE   -> R.drawable.ic_youtube_v4
        AppTarget.FACEBOOK  -> R.drawable.ic_facebook_v4  
        AppTarget.TIKTOK    -> R.drawable.ic_tiktok_v4
        AppTarget.SNAPCHAT  -> R.drawable.ic_snapchat_v4
    }

    val activeGlow = if (isEnabled && !isPremiumLocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (isEnabled) 12f else 0f
                ambientShadowColor = activeGlow
                spotShadowColor = activeGlow
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(36.dp),
        border = BorderStroke(1.dp, if (isEnabled && !isPremiumLocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon Box - Darker background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF23232C))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = appTarget.appName,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isEnabled) 1f else 0.4f)
                    )
                }

                Column {
                    Text(
                        text = appTarget.appName.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = if (isEnabled || isPremiumLocked) Color.White else Color.Gray
                    )
                    if (isPremiumLocked) {
                        Text(
                            "Premium Only",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            if (isPremiumLocked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Premium Feature",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(20.dp)
                )
            } else {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF35343A),
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
