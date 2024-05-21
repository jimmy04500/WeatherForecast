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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edwardkim.weatherforecast.ui.CurrentWeatherSummary
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
                        val items = listOf(AppScreen.WeatherDashboard, AppScreen.Settings)
                        var selectedItem by remember {
                            mutableIntStateOf(0)
                        }
                        NavigationBar{
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItem == index,
                                    onClick = {
                                        selectedItem = index
                                        navController.navigate(item.route) {
//                                            popUpTo(navController.graph.startDestinationId) {
//                                                saveState = true
//                                            }
//                                            launchSingleTop = true
//                                            restoreState = true
                                        }
                                              },
                                    label = { Text(item.name) },
                                    icon = { Icon(
                                        painter = painterResource(id = item.icon),
                                        contentDescription = item.name
                                    ) })
                            }
                        }
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

sealed class AppScreen(val route: String, val name: String, @DrawableRes val icon: Int) {
    object WeatherDashboard: AppScreen("weatherdashboard", "Dashboard", R.drawable.ic_home)
    object Settings: AppScreen("settings", "Settings", R.drawable.ic_settings)
}