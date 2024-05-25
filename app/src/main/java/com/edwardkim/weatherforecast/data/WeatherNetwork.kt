package com.edwardkim.weatherforecast.data

import com.edwardkim.weatherforecast.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherNetwork {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun get5Day3HourForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<ForecastResponse>

    @GET("geo/1.0/direct")
    suspend fun getLocations(
        @Query("q")
        query: String,
        @Query("limit")
        limit: Int = 5,
        @Query("appid")
        apiKey: String = BuildConfig.API_KEY
    ): Response<List<LocationItem>>

    @GET("geo/1.0/reverse")
    suspend fun getLocations(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("limit")
        limit: Int = 1,
        @Query("appid")
        apiKey: String = BuildConfig.API_KEY
    ): Response<List<LocationItem>>
}

data class CurrentWeatherResponse(
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

data class ForecastResponse(
    @SerializedName("list")
    val forecastInfoItems: List<ForecastInfoItem>
)

data class ForecastInfoItem(
    @SerializedName("main")
    val temperatureInfo: TemperatureInfo,
    @SerializedName("weather")
    val weatherInfoItems: List<WeatherInfoItem>,
    @SerializedName("dt")
    val timestamp: Long
)

data class LocationItem(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String?,
    @SerializedName("country")
    val country: String
)