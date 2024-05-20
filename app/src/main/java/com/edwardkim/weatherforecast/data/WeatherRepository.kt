package com.edwardkim.weatherforecast.data

import com.edwardkim.weatherforecast.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRepository {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    @SerializedName("main")
    val temperatureInfo: TemperatureInfo,
    @SerializedName("weather")
    val weatherInfoItems: List<WeatherInfoItem>
)

data class WeatherInfoItem(
    @SerializedName("description")
    val weatherDescription: String
)

data class TemperatureInfo(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLikeTemperature: Double
)