package com.edwardkim.weatherforecast

import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.WeatherForecastItem
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository,
    private val weatherDataStore: DataStore<SavedLocationsData>
): ViewModel() {
    private val latitude: Double = checkNotNull(savedStateHandle["lat"])
    private val longitude: Double = checkNotNull(savedStateHandle["lon"])
    private val locationName: String = checkNotNull(savedStateHandle["locationname"])

    private val _uiState = MutableStateFlow<WeatherDetailUiState>(WeatherDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        updateWeather()
    }

    fun saveLocation() {
        viewModelScope.launch {
            weatherDataStore.updateData { savedWeatherData ->
                val weatherData = WeatherData.newBuilder()
                    .setLocationName(locationName)
                    .setLatitude(latitude)
                    .setLongitude(longitude)
                    .build()
                savedWeatherData.toBuilder()
                    .addWeatherData(weatherData)
                    .build()
            }
        }
    }

    fun updateWeather() {
        viewModelScope.launch {
            val currentWeather = async(Dispatchers.IO) {
                weatherRepository.getCurrentWeather(latitude, longitude, "imperial")
                    .body()
            }
            val forecastWeather = async(Dispatchers.IO) {
                weatherRepository.get5Day3HourForecast(latitude, longitude, "imperial")
                    .body()
            }

            val currentWeatherResponse = currentWeather.await()
            val forecastWeatherResponse = forecastWeather.await()
            if (currentWeatherResponse == null || forecastWeatherResponse == null) {
                _uiState.update {
                    WeatherDetailUiState.Error
                }
                return@launch
            } else {
                _uiState.update {
                    WeatherDetailUiState.Success(
                        locationName = locationName,
                        temperature = currentWeatherResponse.temperatureInfo.temperature,
                        weatherDescription = currentWeatherResponse.weatherInfoItems[0].weatherDescription,
                        feelsLikeTemperature = currentWeatherResponse.temperatureInfo.feelsLikeTemperature,
                        weatherForecastItems = forecastWeatherResponse.forecastInfoItems.map {
                            WeatherForecastItem(
                                time = it.timestamp,
                                temperature = it.temperatureInfo.temperature
                            )
                        }
                    )
                }
            }
        }
    }
}

sealed interface WeatherDetailUiState {
    data object Loading: WeatherDetailUiState
    data object Error: WeatherDetailUiState
    data class Success(
        val locationName: String,
        val temperature: Double,
        val weatherDescription: String,
        val feelsLikeTemperature: Double,
        val weatherForecastItems: List<WeatherForecastItem>
    ): WeatherDetailUiState
}