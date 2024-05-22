package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WeatherForecast(myItems: List<WeatherForecastItem>) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(text = "Header")
            }
            items(myItems) { item ->
                Text(
                    text = item.time,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

data class WeatherForecastItem(
    val time: String,
    val temperature: Double
)


@Preview(showBackground = true)
@Composable
fun PreviewWeatherForecast() {
    WeatherForecast(listOf(
        WeatherForecastItem("5PM", 20.0),
        WeatherForecastItem("6PM", 20.0),
        WeatherForecastItem("7PM", 20.0)
    ))
}