package com.focusguard.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.focusguard.R
import com.focusguard.domain.model.AppTarget
import com.focusguard.domain.model.BlockMode
import com.focusguard.ui.components.AccessibilityBanner
import com.focusguard.ui.components.AppToggleCard
import com.focusguard.ui.components.BreakTimerBottomSheet
import com.focusguard.ui.components.ModeSelector
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showBreakSheet by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAccessibilityService()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showBreakSheet) {
        BreakTimerBottomSheet(
            onDismiss = { showBreakSheet = false },
            onStartBreak = { minutes ->
                viewModel.takeBreak(minutes)
                showBreakSheet = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // To allow seamless scrolling of the vertical app list, we use a scrollable Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FocusGuard",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    val dateFormatted = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.toggleTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (uiState.isDarkTheme == true) "🌙" else "☀️", fontSize = 20.sp)
                }
            }

            // Accessibility Warning Banner
            if (!uiState.isServiceEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A1A)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF3B2525)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                        }
                        Column {
                            Text(
                                "Accessibility access is off — blocking\npaused",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "FIX NOW",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFFFA5A5),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Master Protection Toggle
            val isActive = uiState.blockMode != BlockMode.PAUSED
            Card(
                modifier = Modifier.fillMaxWidth().height(90.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Protection", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error))
                        Text(
                                text = if (isActive) "Blocking active" else "Paused",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                            )
                        }
                        if (isActive) {
                            Text("Click to take a break", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }

                    // Green Switch
                    Switch(
                        checked = isActive,
                        onCheckedChange = { if (isActive) showBreakSheet = true else viewModel.setBlockMode(BlockMode.BLOCK_ALL) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    value = "${uiState.todayBlockCount}",
                    label = "BLOCKED\nTODAY",
                    modifier = Modifier.weight(1f),
                    indicatorColor = null,
                    valueColor = Color.White
                )
                StatCard(
                    value = "${uiState.streakDays}",
                    label = "DAY STREAK",
                    modifier = Modifier.weight(1f),
                    indicatorColor = MaterialTheme.colorScheme.error,
                    valueColor = Color.White
                )
                StatCard(
                    value = "${(uiState.todayBlockCount * 0.5).toInt()}m",
                    label = "SAVED TODAY",
                    modifier = Modifier.weight(1f),
                    indicatorColor = null,
                    valueColor = MaterialTheme.colorScheme.secondary
                )
            }

            // Block Mode Selector
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Block mode", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                
                // Segmented Control Pill
                ModeSelector(
                    selectedMode = uiState.blockMode,
                    onModeSelected = { viewModel.setBlockMode(it) }
                )
                
                val contextText = when(uiState.blockMode) {
                    BlockMode.BLOCK_ALL -> "All distracting apps are currently restricted. Only essential tools are available."
                    BlockMode.CURIOUS -> "Curious mode enabled. You can open apps up to ${uiState.curiousRemaining} more times."
                    BlockMode.PAUSED -> "Protection is currently paused."
                }
                Text(contextText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
            }

            // Protected Apps List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Protected apps", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Edit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onNavigateToSettings() })
                }
                
                // Vertical Apps
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTarget.values().forEach { target ->
                        val isEnabled = uiState.appEnabledStates[target.packageName] ?: true
                        val isPremiumLocked = (target == AppTarget.FACEBOOK || target == AppTarget.SNAPCHAT) && !uiState.isPremium
                        
                        AppToggleCard(
                            appTarget = target,
                            isEnabled = isEnabled,
                            isPremiumLocked = isPremiumLocked,
                            onToggle = { viewModel.toggleApp(target, it) }
                        )
                    }
                }
            }

            // Break Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { showBreakSheet = true }
                    .padding(horizontal = 4.dp), 
                contentAlignment = Alignment.Center
            ) {
                // Dashed border effect
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                    )
                    drawRoundRect(
                        color = Color(0xFF2C2C35),
                        size = size,
                        cornerRadius = CornerRadius(size.height / 2, size.height / 2),
                        style = stroke
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("☕", fontSize = 16.sp)
                    Text("TAKE A 5-MIN BREAK", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
        }
    }
}

@Composable
fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier, indicatorColor: Color? = null) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
                if (indicatorColor != null) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(indicatorColor))
                }
            }
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 12.sp)
        }
    }
}

