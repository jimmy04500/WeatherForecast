package com.edwardkim.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edwardkim.weatherforecast.WeatherForecastItem

@Composable
fun WeatherForecast(myItems: List<WeatherForecastItem>) {
    Card(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        LazyColumn(
            modifier = Modifier.padding(24.dp)
        ) {
            // TODO fix scroll lagging
            val itemsLength = myItems.size
            itemsIndexed(myItems) { index, item ->
                val dayOfWeek = item.dayOfWeek
                if (index == 0 || dayOfWeek != myItems[index-1].dayOfWeek) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = if (index > 0) Modifier.padding(top = 16.dp) else Modifier
                    )
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                    (if (index < itemsLength - 1)
                        Modifier.padding(vertical = 8.dp)
                    else
                        Modifier.padding(top = 8.dp))
                        .fillMaxWidth()
                ) {
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "${item.temperature}°",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.End
                    )
                }
                if (index < itemsLength - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherForecast() {
    WeatherForecast(listOf(
        WeatherForecastItem("Monday", "5PM", "75"),
        WeatherForecastItem("Monday", "11PM", "65"),
        WeatherForecastItem("Tuesday", "1AM", "78"),
        WeatherForecastItem("Wednesday", "12PM", "70")
    ))
}