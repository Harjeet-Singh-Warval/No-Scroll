package com.focusguard.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusguard.ui.components.StatsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your stats", fontWeight = FontWeight.Bold) },
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

            // SECTION 1 - Week overview
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "This week",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    WeeklyBarChart(uiState.weeklyData, uiState.todayIndex)
                }
            }

            // SECTION 2 - Summary cards Row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatsCard(title = "Total blocked", value = "${uiState.totalBlockedAllTime}")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatsCard(title = "Best day", value = "${uiState.bestDayCount}", subtitle = uiState.bestDayName)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatsCard(title = "Longest streak", value = "${uiState.longestStreak}", subtitle = "days")
                    }
                }
            }

            // SECTION 3 - Per-app breakdown
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "By app",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    val maxCount = uiState.appBreakdown.maxOfOrNull { it.second }?.toFloat() ?: 1f
                    
                    uiState.appBreakdown.forEach { (appName, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                appName,
                                modifier = Modifier.weight(0.3f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(count / maxCount)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            Text(
                                count.toString(),
                                modifier = Modifier.weight(0.1f).padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // SECTION 4 - Time saved estimate
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("⏳", fontSize = 42.sp)
                        Column {
                            Text(
                                "Time saved this week",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val hours = uiState.totalTimeSavedMinutes / 60
                            val mins = uiState.totalTimeSavedMinutes % 60
                            Text(
                                text = if (hours > 0) "${hours}h ${mins}m" else "${mins}m",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                "Based on avg. 35 sec per reel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun WeeklyBarChart(data: List<Pair<String, Int>>, todayIndex: Int) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { it.second }.coerceAtLeast(1)
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val barWidth = 24.dp.toPx()
        val spacing = (size.width - (barWidth * 7)) / 6
        val chartHeight = size.height - 30.dp.toPx() // leave room for text

        data.forEachIndexed { index, pair ->
            val label = pair.first
            val value = pair.second
            
            // Calculate bar height proportionally
            // Highlight today's bar by making it slightly taller base or distinct color if preferred.
            // Requirement: "Today's bar = slightly taller highlight or Primary (#6C63FF)"
            val proportion = value / maxVal.toFloat()
            var barHeight = proportion * chartHeight
            if (barHeight < 4.dp.toPx()) barHeight = 4.dp.toPx() // minimum height

            val xOffset = index * (barWidth + spacing)
            val yOffset = chartHeight - barHeight

            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(12.dp.toPx())
            )

            // Draw X Axis labels
            // (Using legacy drawContext wrapper for text to keep it simple without TextMeasurer in all OS)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                xOffset + (barWidth / 2f),
                size.height - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(
                        (labelColor.alpha * 255).toInt(),
                        (labelColor.red * 255).toInt(),
                        (labelColor.green * 255).toInt(),
                        (labelColor.blue * 255).toInt()
                    )
                    textSize = 34f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
