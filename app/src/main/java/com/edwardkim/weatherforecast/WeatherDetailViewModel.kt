package com.edwardkim.weatherforecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.LocationName
import com.edwardkim.weatherforecast.data.WeatherForecastItem
import com.edwardkim.weatherforecast.data.WeatherInfo
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository
): ViewModel() {
    private val latitude: Double = checkNotNull(savedStateHandle["lat"])
    private val longitude: Double = checkNotNull(savedStateHandle["lon"])
    private val locationName: String = checkNotNull(savedStateHandle["locationname"])

    private val _uiState = MutableStateFlow<WeatherDetailUiState>(WeatherDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var weatherInfo: WeatherInfo

    init {
        updateWeather()
    }

    fun saveLocation() {
        viewModelScope.launch {
            weatherRepository.addLocation(latitude, longitude, LocationName(locationName, ""), weatherInfo)
        }
    }

    private fun updateWeather() {
        viewModelScope.launch {
            weatherInfo = weatherRepository.getWeather(latitude, longitude)
            // TODO handle error case
            _uiState.update {
                WeatherDetailUiState.Success(
                    locationName = locationName,
                    temperature = weatherInfo.temperature,
                    weatherDescription = weatherInfo.weatherDescription,
                    feelsLikeTemperature = weatherInfo.feelsLikeTemp,
                    weatherForecastItems = weatherInfo.forecast.map {
                        WeatherForecastItem(
                            time = it.time,
                            temperature = it.temperature
                        )
                    }
                )
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