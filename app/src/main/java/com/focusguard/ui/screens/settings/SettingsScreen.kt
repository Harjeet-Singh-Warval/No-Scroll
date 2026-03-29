package com.focusguard.ui.screens.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusguard.domain.model.AppTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showActionSheet by remember { mutableStateOf(false) }

    if (showActionSheet) {
        ModalBottomSheet(onDismissRequest = { showActionSheet = false }, containerColor = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Block action", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                val actions = listOf("CLOSE_VIDEO" to "Close video (go back)", "EXIT_APP" to "Exit app (go home)", "LOCK_SCREEN" to "Lock screen")
                actions.forEach { (id, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setBlockAction(id); showActionSheet = false }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = uiState.blockAction == id, onClick = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // GROUP 1 - Appearance
            item {
                SettingsGroup(title = "Appearance") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Theme", style = MaterialTheme.typography.bodyLarge)
                        
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val options = listOf(false to "Light", true to "Dark", null to "System")
                            options.forEach { (value, label) ->
                                val selected = uiState.isDarkTheme == value
                                val bg by animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                val tc by animateColorAsState(if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bg)
                                        .clickable { viewModel.setTheme(value) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(label, color = tc, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }

            // GROUP 2 - Protection
            item {
                SettingsGroup(title = "Protection") {
                    SettingsRow(
                        title = "Block action",
                        subtitle = when(uiState.blockAction) {
                            "CLOSE_VIDEO" -> "Close video (go back)"
                            "EXIT_APP" -> "Exit app (go home)"
                            "LOCK_SCREEN" -> "Lock screen"
                            else -> "Unknown"
                        },
                        onClick = { showActionSheet = true }
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Curious limit", style = MaterialTheme.typography.bodyLarge)
                            Text("Allow N reels before blocking", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (uiState.curiousLimit > 1) viewModel.setCuriousLimit(uiState.curiousLimit - 1) }) {
                                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(uiState.curiousLimit.toString(), style = MaterialTheme.typography.titleLarge)
                            IconButton(onClick = { if (uiState.curiousLimit < 10) viewModel.setCuriousLimit(uiState.curiousLimit + 1) }) {
                                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    SettingsSwitchRow(
                        title = "PIN protection",
                        subtitle = "Require PIN to change settings",
                        checked = uiState.pinProtection != null,
                        onCheckedChange = { if (it) viewModel.setPin("0000") else viewModel.setPin(null) }
                    )
                }
            }

            // GROUP 3 - Protected Apps
            item {
                SettingsGroup(title = "Protected apps") {
                    AppTarget.values().forEach { target ->
                        val isLocked = (target == AppTarget.FACEBOOK || target == AppTarget.SNAPCHAT) && !uiState.isPremium
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                val emoji = when(target) {
                                    AppTarget.INSTAGRAM -> "📸"
                                    AppTarget.YOUTUBE -> "▶️"
                                    AppTarget.FACEBOOK -> "🟦"
                                    AppTarget.TIKTOK -> "🎵"
                                    AppTarget.SNAPCHAT -> "👻"
                                }
                                Text(emoji, fontSize = 24.sp)
                                Text(target.appName, style = MaterialTheme.typography.bodyLarge)
                            }
                            if (isLocked) {
                                Icon(Icons.Default.Lock, contentDescription = "Premium", tint = MaterialTheme.colorScheme.primary)
                            } else {
                                val enabled = uiState.appEnabledStates[target.packageName] ?: true
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { viewModel.setAppEnabled(target.packageName, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // GROUP 4 - Battery & Permissions
            item {
                SettingsGroup(title = "Battery & Permissions") {
                    val isServiceActive = uiState.isBatteryOptimized // Approximation for now, or just use another check
                    SettingsStatusRow(
                        title = "Accessibility access",
                        statusText = "Check status",
                        isGood = true,
                        onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                    )
                    SettingsStatusRow(
                        title = "Battery optimization",
                        statusText = if (uiState.isBatteryOptimized) "Restricted" else "Excluded",
                        isGood = !uiState.isBatteryOptimized,
                        onClick = { context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }
                    )
                    SettingsSwitchRow(
                        title = "Auto-start on reboot",
                        subtitle = "Keep FocusGuard active after restart",
                        checked = uiState.isAutoStartEnabled,
                        onCheckedChange = { viewModel.setAutoStartEnabled(it) }
                    )
                }
            }

            // GROUP 5 - About
            item {
                SettingsGroup(title = "About", isLast = true) {
                    SettingsNavRow(title = "Version", value = "1.0.0")
                    SettingsNavRow(title = "Rate the app")
                    SettingsNavRow(title = "Privacy policy")
                    SettingsNavRow(
                        title = "Reset all data", 
                        valueColor = MaterialTheme.colorScheme.error,
                        onClick = { viewModel.resetAllData() }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, isLast: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
    if (!isLast) Spacer(modifier = Modifier.height(24.dp)) else Spacer(modifier = Modifier.height(64.dp))
}

@Composable
fun SettingsRow(title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SettingsSwitchRow(title: String, subtitle: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun SettingsStatusRow(title: String, statusText: String, isGood: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        val bgColor = if (isGood) MaterialTheme.colorScheme.tertiary.copy(alpha=0.15f) else MaterialTheme.colorScheme.error.copy(alpha=0.15f)
        val textColor = if (isGood) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(statusText, style = MaterialTheme.typography.labelSmall, color = textColor)
        }
    }
}

@Composable
fun SettingsNavRow(
    title: String, 
    value: String? = null, 
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = valueColor)
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
