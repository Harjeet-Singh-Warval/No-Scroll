package com.focusguard.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.focusguard.domain.repository.SettingsRepository
import com.focusguard.ui.screens.home.HomeScreen
import com.focusguard.ui.screens.onboarding.OnboardingScreen
import com.focusguard.ui.screens.settings.SettingsScreen
import com.focusguard.ui.screens.stats.StatsScreen
import com.focusguard.ui.theme.StopScrollTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("first_launch", true)
        
        setContent {
            val isDarkThemePref by settingsRepository.isDarkTheme.collectAsState(initial = null)
            val useDarkTheme = isDarkThemePref ?: isSystemInDarkTheme()

            StopScrollTheme(darkTheme = useDarkTheme) {
                MainAppScreen(
                    startDestination = if (isFirstLaunch) "onboarding" else "main"
                ) {
                    sharedPrefs.edit().putBoolean("first_launch", false).apply()
                }
            }
        }
    }
}

@Composable
fun MainAppScreen(startDestination: String, onOnboardingComplete: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(onComplete = {
                onOnboardingComplete()
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("main") {
            MainTabsScreen()
        }
    }
}

@Composable
fun MainTabsScreen() {
    val navController = rememberNavController()
    
    val items = listOf(
        Triple("home", "Home", Icons.Filled.Home),
        Triple("stats", "Stats", Icons.Filled.Star),
        Triple("settings", "Settings", Icons.Filled.Settings)
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(36.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.first } == true
                        val iconColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null, 
                                    onClick = {
                                        navController.navigate(screen.first) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.third,
                                contentDescription = screen.second,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = "home", 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(onNavigateToSettings = { navController.navigate("settings") }) }
            composable("stats") { StatsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
