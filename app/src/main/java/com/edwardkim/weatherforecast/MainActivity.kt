package com.edwardkim.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edwardkim.weatherforecast.ui.AppNavigationBar
import com.edwardkim.weatherforecast.ui.CurrentWeatherSummary
import com.edwardkim.weatherforecast.ui.NavigationBarItemInfo
import com.edwardkim.weatherforecast.ui.SettingsScreen
import com.edwardkim.weatherforecast.ui.theme.WeatherForecastTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val weatherDashboardVm: WeatherDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherForecastTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        val screens = listOf(AppScreen.WeatherDashboard, AppScreen.Settings)
                        val items = screens.map {
                            NavigationBarItemInfo(
                                it.label,
                                it.icon,
                                it.label
                            )
                        }
                        AppNavigationBar(
                            items = items,
                            onItemClick = {
                                navController.navigate(screens[it].route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppScreen.WeatherDashboard.route,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppScreen.WeatherDashboard.route) {
                            CurrentWeatherSummary(
                                viewModel = weatherDashboardVm,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable(AppScreen.Settings.route) {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // TODO move lifecycle code to viewmodel
        weatherDashboardVm.updateWeather()
    }
}

sealed class AppScreen(val route: String, val label: String, @DrawableRes val icon: Int) {
    object WeatherDashboard: AppScreen("weatherdashboard", "Dashboard", R.drawable.ic_home)
    object Settings: AppScreen("settings", "Settings", R.drawable.ic_settings)
}