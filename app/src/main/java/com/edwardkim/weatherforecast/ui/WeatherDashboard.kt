package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edwardkim.weatherforecast.WeatherDashboardUiState
import com.edwardkim.weatherforecast.WeatherDashboardViewModel
import com.edwardkim.weatherforecast.WeatherForecastItem

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
                weatherForecastItems = uiState.weatherForecastItems,
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
    weatherForecastItems: List<WeatherForecastItem>,
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
            myItems = weatherForecastItems
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherDashboard() {
    WeatherDashboard(
        currentTemperature = 20.0,
        currentWeatherDescription = "Sunny",
        currentFeelsLikeTemperature = 20.0,
        weatherForecastItems = listOf(
            WeatherForecastItem("Monday", "5PM", "75"),
            WeatherForecastItem("Monday", "11PM", "65"),
            WeatherForecastItem("Tuesday", "1AM", "78"),
            WeatherForecastItem("Wednesday", "12PM", "70")
        )
    )
}