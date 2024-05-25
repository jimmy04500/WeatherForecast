package com.edwardkim.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edwardkim.weatherforecast.ui.AppNavigationBar
import com.edwardkim.weatherforecast.ui.DashboardScreen
import com.edwardkim.weatherforecast.ui.LocationsScreen
import com.edwardkim.weatherforecast.ui.NavigationBarItemInfo
import com.edwardkim.weatherforecast.ui.SettingsScreen
import com.edwardkim.weatherforecast.ui.weatherdetail.SearchedWeatherDetail
import com.edwardkim.weatherforecast.ui.theme.WeatherForecastTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val weatherDashboardVm: DashboardViewModel by viewModels()
    private val topLevelScreens = listOf(AppScreen.WeatherDashboard, AppScreen.WeatherList, AppScreen.Settings)
    private val topLevelScreenRoutes = topLevelScreens.map { it.route }
    private val topLevelScreenNavBarItems = topLevelScreens.map {
        NavigationBarItemInfo(it.label, it.icon, it.label)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherForecastTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                Scaffold(
                    bottomBar = {
                        if (currentRoute in topLevelScreenRoutes) {
                            AppNavigationBar(
                                items = topLevelScreenNavBarItems,
                                onNavigateToItem = {
                                    navigateToTopLevel(navController, topLevelScreens[it].route)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppScreen.WeatherDashboard.route,
                        enterTransition = { fadeIn(animationSpec = tween(1)) },
                        exitTransition = { fadeOut(animationSpec = tween(1)) },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppScreen.WeatherDashboard.route) {
                            DashboardScreen(
                                viewModel = weatherDashboardVm
                            )
                        }
                        composable(AppScreen.WeatherList.route) {
                            LocationsScreen(
                                onNavigateToWeatherDetail =
                                { lat, lon, locationName ->
                                    navController.navigate("weatherDetail/$lat/$lon/$locationName")
                                }
                            )
                        }
                        composable(AppScreen.Settings.route) {
                            SettingsScreen()
                        }
                        composable(
                            route = "weatherDetail/{lat}/{lon}/{locationname}",
                            arguments = listOf(
                                navArgument("lat") { type = NavType.FloatType },
                                navArgument("lon") { type = NavType.FloatType },
                                navArgument("locationname") { type = NavType.StringType }
                            )
                        ) {
                            SearchedWeatherDetail(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateToTopLevel(navController: NavHostController, route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun onStart() {
        super.onStart()
        // TODO move lifecycle code to viewmodel
        weatherDashboardVm.updateWeather()
    }
}

sealed class AppScreen(val route: String, val label: String, @DrawableRes val icon: Int) {
    data object WeatherDashboard: AppScreen("weatherdashboard", "Dashboard", R.drawable.ic_home)
    data object WeatherList: AppScreen("weatherlist", "Locations", R.drawable.ic_list)
    data object Settings: AppScreen("settings", "Settings", R.drawable.ic_settings)
}