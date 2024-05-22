package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edwardkim.weatherforecast.WeatherDashboardUiState
import com.edwardkim.weatherforecast.WeatherDashboardViewModel

@Composable
fun WeatherDashboard(
    viewModel: WeatherDashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        WeatherDashboardUiState.Loading -> {
            Loading()
        }
        WeatherDashboardUiState.RequestLocationPermission -> {
            // TODO move permission logic to Activity
            LocationPermissionRequester(
                onPermissionGranted = {
                    viewModel.onLocationPermissionGranted()
                },
                onPermissionDeniedPermanently = {
                    viewModel.onLocationPermissionDeniedPermanently()
                }
            )
        }
        WeatherDashboardUiState.LocationPermissionDeniedPermanently -> {
            LocationPermissionDenied()
        }
        WeatherDashboardUiState.Error -> TODO()
        is WeatherDashboardUiState.Success -> {
            WeatherDashboard(
                currentTemperature = uiState.currentTemperature,
                currentWeatherDescription = uiState.currentWeatherDescription,
                currentFeelsLikeTemperature = uiState.currentFeelsLikeTemperature,
                modifier = modifier
            )
        }
    }
}

@Composable
fun WeatherDashboard(
    currentTemperature: Double,
    currentWeatherDescription: String,
    currentFeelsLikeTemperature: Double,
    modifier: Modifier = Modifier
) {
    Column {
        CurrentWeatherSummary(
            city = "Chicago",
            temperature = currentTemperature,
            weatherDescription = currentWeatherDescription,
            feelsLikeTemp = currentFeelsLikeTemperature,
            modifier = modifier
        )
        WeatherForecast(
            myItems = listOf(
                WeatherForecastItem("5PM", 20.0),
                WeatherForecastItem("6PM", 20.0),
                WeatherForecastItem("7PM", 20.0)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherDashboard() {
    WeatherDashboard(
        currentTemperature = 20.0,
        currentWeatherDescription = "Sunny",
        currentFeelsLikeTemperature = 20.0
    )
}