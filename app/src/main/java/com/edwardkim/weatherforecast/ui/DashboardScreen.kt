package com.edwardkim.weatherforecast.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edwardkim.weatherforecast.DashboardViewModel
import com.edwardkim.weatherforecast.WeatherDashboardUiState
import com.edwardkim.weatherforecast.data.WeatherForecastItem
import com.edwardkim.weatherforecast.ui.weatherdetail.Loading
import com.edwardkim.weatherforecast.ui.weatherdetail.WeatherDetail

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        WeatherDashboardUiState.Loading -> {
            Loading()
        }
        WeatherDashboardUiState.Error -> TODO()
        is WeatherDashboardUiState.Success -> {
            WeatherDetail(
                locationName = uiState.locationName,
                temperature = uiState.temperature,
                weatherDescription = uiState.weatherDescription,
                feelsLikeTemperature = uiState.feelsLikeTemperature,
                weatherForecastItems = uiState.weatherForecastItems
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherDashboard() {
    WeatherDetail(
        locationName = "London",
        temperature = 20.0,
        weatherDescription = "Sunny",
        feelsLikeTemperature = 20.0,
        weatherForecastItems = listOf(
            WeatherForecastItem(1716390000, 20.0),
            WeatherForecastItem(1716404400, 20.0),
            WeatherForecastItem(1716462000, 20.0)
        )
    )
}