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
import com.edwardkim.weatherforecast.data.LocationRepository
import com.edwardkim.weatherforecast.ui.AppNavigationBar
import com.edwardkim.weatherforecast.ui.DashboardScreen
import com.edwardkim.weatherforecast.ui.LocationsScreen
import com.edwardkim.weatherforecast.ui.NavigationBarItemInfo
import com.edwardkim.weatherforecast.ui.SettingsScreen
import com.edwardkim.weatherforecast.ui.theme.WeatherForecastTheme
import com.edwardkim.weatherforecast.ui.weatherdetail.LocationPermissionDenied
import com.edwardkim.weatherforecast.ui.weatherdetail.LocationPermissionRequester
import com.edwardkim.weatherforecast.ui.weatherdetail.SearchedWeatherDetail
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var locationRepository: LocationRepository

    private val weatherDashboardVm: DashboardViewModel by viewModels()
    private val topLevelScreens = listOf(TopLevelScreen.WeatherDashboard, TopLevelScreen.WeatherList, TopLevelScreen.Settings)
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
                        startDestination = TopLevelScreen.WeatherDashboard.route,
                        enterTransition = { fadeIn(animationSpec = tween(1)) },
                        exitTransition = { fadeOut(animationSpec = tween(1)) },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("LocationPermissionDenied") {
                            LocationPermissionDenied()
                        }
                        composable("LocationPermissionRequest") {
                            LocationPermissionRequester(
                                onPermissionGranted = {
                                    navController.popBackStack()
                                    weatherDashboardVm.updateWeather()
                                },
                                onPermissionDeniedPermanently = {
                                    navController.navigate("LocationPermissionDenied")
                                }
                            )
                        }
                        composable(TopLevelScreen.WeatherDashboard.route) {
                            if (!locationRepository.hasLocationPermission()) {
                                navController.navigate("LocationPermissionRequest")
                            } else {
                                DashboardScreen(
                                    viewModel = weatherDashboardVm
                                )
                            }
                        }
                        composable(TopLevelScreen.WeatherList.route) {
                            LocationsScreen(
                                onNavigateToWeatherDetail =
                                { lat, lon, locationName ->
                                    navController.navigate("weatherDetail/$lat/$lon/$locationName")
                                }
                            )
                        }
                        composable(TopLevelScreen.Settings.route) {
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

sealed class TopLevelScreen(val route: String, val label: String, @DrawableRes val icon: Int) {
    data object WeatherDashboard: TopLevelScreen("weatherdashboard", "Dashboard", R.drawable.ic_home)
    data object WeatherList: TopLevelScreen("weatherlist", "Locations", R.drawable.ic_list)
    data object Settings: TopLevelScreen("settings", "Settings", R.drawable.ic_settings)
}
