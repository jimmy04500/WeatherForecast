package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.LocationRepository
import com.edwardkim.weatherforecast.data.WeatherForecastItem
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
): ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = weatherRepository.weatherData.flatMapConcat {
        if (it.isEmpty()) {
            saveCurrentLocation()
            flowOf(WeatherDashboardUiState.Loading)
        } else {
            val firstItem = it.first()
            flowOf(WeatherDashboardUiState.Success(
                locationName = firstItem.name.name,
                temperature = firstItem.temperature,
                weatherDescription = firstItem.weatherDescription,
                feelsLikeTemperature = firstItem.feelsLikeTemp,
                weatherForecastItems = firstItem.forecast
            ))
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeatherDashboardUiState.Loading
        )

    private fun saveCurrentLocation() {
        viewModelScope.launch {
            val location = withContext(Dispatchers.IO) {
                locationRepository.getLocation()
            }
            if (location == null) {
//                _uiState.update {
//                    WeatherDashboardUiState.Error
//                }
                return@launch
            }
            weatherRepository.addLocation(location.latitude, location.longitude)
        }
    }

    fun updateWeather() {
        viewModelScope.launch {
            weatherRepository.updateWeather()
        }
    }
}

sealed interface WeatherDashboardUiState {
    data object Loading: WeatherDashboardUiState
    data object Error: WeatherDashboardUiState
    data class Success(
        val locationName: String,
        val temperature: Double,
        val weatherDescription: String,
        val feelsLikeTemperature: Double,
        val weatherForecastItems: List<WeatherForecastItem>
    ): WeatherDashboardUiState
}