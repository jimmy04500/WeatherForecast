package com.edwardkim.weatherforecast.data

import androidx.datastore.core.DataStore
import com.edwardkim.weatherforecast.ForecastData
import com.edwardkim.weatherforecast.SavedLocationsData
import com.edwardkim.weatherforecast.WeatherData
import com.edwardkim.weatherforecast.toRegionString
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherNetwork: WeatherNetwork,
    private val weatherDataStore: DataStore<SavedLocationsData>
) {
    val weatherData = weatherDataStore.data.map { savedWeatherData ->
        savedWeatherData.weatherDataList.map { weatherData ->
            WeatherInfo(
                lat = weatherData.latitude,
                lon = weatherData.longitude,
                name = LocationName(weatherData.name, ""),
                temperature = weatherData.temperature,
                weatherDescription = weatherData.weatherDescription,
                feelsLikeTemp = weatherData.feelsLikeTemp,
                forecast = weatherData.forecastDataList.map { forecastData ->
                    WeatherForecastItem(
                        time = forecastData.time,
                        temperature = forecastData.temperature
                    )
                }
            )
        }
    }

    suspend fun searchLocations(query: String): List<LocationItem>? {
        return weatherNetwork.getLocations(query).body()
    }

    suspend fun getWeather(lat: Double, lon: Double, name: LocationName? = null): WeatherInfo {
        val name = if (name != null) name else {
            val location = weatherNetwork.getLocations(lat, lon).body()
                ?: throw Exception("Location not found")
            LocationName(location[0].name, toRegionString(location[0].state, location[0].country))
        }
        // TODO change to async calls
        val currentWeather = weatherNetwork.getCurrentWeather(lat, lon, "imperial")
            .body()
        val forecastWeather = weatherNetwork.get5Day3HourForecast(lat, lon, "imperial")
            .body()
        if (currentWeather == null || forecastWeather == null) {
            throw Exception("Weather not found")
        }
        return WeatherInfo(
            lat = lat,
            lon = lon,
            name = name,
            temperature = currentWeather.temperatureInfo.temperature,
            weatherDescription = currentWeather.weatherInfoItems[0].weatherDescription,
            feelsLikeTemp = currentWeather.temperatureInfo.feelsLikeTemperature,
            forecast = forecastWeather.forecastInfoItems.map {
                WeatherForecastItem(
                    time = it.timestamp,
                    temperature = it.temperatureInfo.temperature
                )
            }
        )
    }

    suspend fun addLocation(lat: Double, lon: Double, name: LocationName? = null, weatherInfo: WeatherInfo? = null) {
        addWeatherInfo(weatherInfo ?: getWeather(lat, lon, name))
    }

    private suspend fun addWeatherInfo(weatherInfo: WeatherInfo) {
        val weatherData = getWeatherData(weatherInfo)
        weatherDataStore.updateData {
            it.toBuilder()
                .addWeatherData(weatherData)
                .build()
        }
    }

    private suspend fun updateWeatherInfo(weatherInfo: WeatherInfo, index: Int) {
        val weatherData = getWeatherData(weatherInfo)
        weatherDataStore.updateData {
            it.toBuilder()
                .setWeatherData(index, weatherData)
                .build()
        }
    }

    private fun getWeatherData(weatherInfo: WeatherInfo): WeatherData {
        val weatherDataBuilder = WeatherData.newBuilder()
            .setLatitude(weatherInfo.lat)
            .setLongitude(weatherInfo.lon)
            // TODO change to include name and region
            .setName(weatherInfo.name.name)
            .setTemperature(weatherInfo.temperature)
            .setWeatherDescription(weatherInfo.weatherDescription)
            .setFeelsLikeTemp(weatherInfo.feelsLikeTemp)
        weatherInfo.forecast.forEach {
            val forecastData = ForecastData.newBuilder()
                .setTemperature(it.temperature)
                .setTime(it.time)
                .build()
            weatherDataBuilder.addForecastData(forecastData)
        }
        return weatherDataBuilder.build()
    }

    suspend fun updateWeather() {
        weatherDataStore.data.collect {
            it.weatherDataList.forEachIndexed { index, weatherData ->
                val newWeather = getWeather(
                    weatherData.latitude,
                    weatherData.longitude,
                    LocationName(weatherData.name, "")
                )
                updateWeatherInfo(newWeather, index)
            }
        }
    }
}

data class LocationName(
    val name: String,
    val region: String
)