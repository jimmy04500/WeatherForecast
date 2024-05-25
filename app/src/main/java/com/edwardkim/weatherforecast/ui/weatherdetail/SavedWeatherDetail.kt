package com.edwardkim.weatherforecast.ui.weatherdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.edwardkim.weatherforecast.data.WeatherForecastItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWeatherDetail(
    locationName: String,
    temperature: Double,
    weatherDescription: String,
    feelsLikeTemperature: Double,
    weatherForecastItems: List<WeatherForecastItem>,
    onNavigateBack: () -> Unit
) {
    Column {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = "Weather")
            }
        )
        WeatherDetail(
            locationName = locationName,
            temperature = temperature,
            weatherDescription = weatherDescription,
            feelsLikeTemperature = feelsLikeTemperature,
            weatherForecastItems = weatherForecastItems
        )
    }
}