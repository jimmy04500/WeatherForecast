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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

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
            val itemLength = myItems.size
            itemsIndexed(myItems) { index, item ->
                val dayOfWeek = toDayOfWeekString(item.time)
                if (index == 0 || dayOfWeek != toDayOfWeekString(myItems[index-1].time)) {
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
                    (if (index < itemLength - 1)
                        Modifier.padding(vertical = 8.dp)
                    else
                        Modifier.padding(top = 8.dp))
                        .fillMaxWidth()
                ) {
                    Text(
                        text = toLocalHourString(item.time),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "${item.temperature.roundToInt()}°",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.End
                    )
                }
                if (index < itemLength - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

fun toLocalHourString(time: Long): String {
    return Instant
        .ofEpochSecond(time)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("ha"))
        .toString()
}

fun toDayOfWeekString(time: Long): String {
    return Instant
        .ofEpochSecond(time)
        .atZone(ZoneId.systemDefault())
        .dayOfWeek
        .toString()
}

data class WeatherForecastItem(
    val time: Long,
    val temperature: Double
)


@Preview(showBackground = true)
@Composable
fun PreviewWeatherForecast() {
    WeatherForecast(listOf(
        WeatherForecastItem(1716390000, 20.0),
        WeatherForecastItem(1716404400, 20.0),
        WeatherForecastItem(1716462000, 20.0),
        WeatherForecastItem(1716548400, 20.0)
    ))
}