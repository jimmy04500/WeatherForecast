package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edwardkim.weatherforecast.WeatherDashboardViewModel

@Composable
fun CurrentWeatherSummary(
    viewModel: WeatherDashboardViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTemp = viewModel.currentTemperature.collectAsStateWithLifecycle()
    val currentWeatherDescription = viewModel.currentWeatherDescription.collectAsStateWithLifecycle()
    val currentFeelsLikeTemp = viewModel.currentFeelsLikeTemperature.collectAsStateWithLifecycle()
    CurrentWeatherSummary(
        city = "Chicago",
        temperature = currentTemp.value,
        weatherDescription = currentWeatherDescription.value,
        feelsLikeTemp = currentFeelsLikeTemp.value,
        onClick = {
            viewModel.updateWeather()
        },
        modifier = modifier
    )
}

@Composable
fun CurrentWeatherSummary(
    city: String,
    temperature: Double,
    weatherDescription: String,
    feelsLikeTemp: Double,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = city,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = weatherDescription,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Feels like $feelsLikeTemp°",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Update Weather")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrentWeatherSummary() {
    CurrentWeatherSummary(
        city = "San Francisco",
        temperature = 72.0,
        weatherDescription = "Sunny",
        feelsLikeTemp = 80.0)
}
