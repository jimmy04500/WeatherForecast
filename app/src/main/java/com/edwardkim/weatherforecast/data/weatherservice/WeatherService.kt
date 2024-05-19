package com.edwardkim.weatherforecast.data.weatherservice

import com.edwardkim.weatherforecast.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    @SerializedName("main")
    val temperatureInfo: TemperatureInfo
)

data class TemperatureInfo(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("temp_min")
    val minTemperature: Double,
    @SerializedName("temp_max")
    val maxTemperature: Double,
)