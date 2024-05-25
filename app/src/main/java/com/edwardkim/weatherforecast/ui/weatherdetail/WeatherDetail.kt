package com.edwardkim.weatherforecast.ui.weatherdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.edwardkim.weatherforecast.data.WeatherForecastItem

@Composable
fun WeatherDetail(
    locationName: String,
    temperature: Double,
    weatherDescription: String,
    feelsLikeTemperature: Double,
    weatherForecastItems: List<WeatherForecastItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        WeatherSummary(
            locationName = locationName,
            temperature = temperature,
            weatherDescription = weatherDescription,
            feelsLikeTemp = feelsLikeTemperature,
            modifier = modifier
        )
        WeatherForecast(
            weatherForecastItems = weatherForecastItems
        )
    }
}