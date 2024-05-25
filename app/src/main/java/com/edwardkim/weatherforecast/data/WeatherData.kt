package com.edwardkim.weatherforecast.data

data class CurrentWeather(
    val name: String,
    val state: String?,
    val country: String,
    val temperature: Double,
    val weatherDescription: String,
    val feelsLikeTemp: Double
)

data class WeatherForecast(
    val items: List<WeatherForecastItem>
)

data class WeatherForecastItem(
    val time: Long,
    val temperature: Double
)

data class WeatherInfo(
    val lat: Double,
    val lon: Double,
    val name: LocationName,
    val temperature: Double,
    val weatherDescription: String,
    val feelsLikeTemp: Double,
    val forecast: List<WeatherForecastItem>
)