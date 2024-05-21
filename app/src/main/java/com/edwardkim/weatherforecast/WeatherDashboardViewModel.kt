package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.LocationRepository
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherDashboardViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<WeatherDashboardUiState>(WeatherDashboardUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onLocationPermissionGranted() {
        updateWeather()
    }

    fun onLocationPermissionDeniedPermanently() {
        _uiState.update {
            WeatherDashboardUiState.LocationPermissionDeniedPermanently
        }
    }

    fun updateWeather() {
        viewModelScope.launch {
            // TODO cache / store location
            _uiState.update {
                WeatherDashboardUiState.Loading
            }

            if (!locationRepository.hasLocationPermission()) {
                _uiState.update {
                    WeatherDashboardUiState.RequestLocationPermission
                }
                return@launch
            }

            val location = withContext(Dispatchers.IO) {
                locationRepository.getLocation()
            }
            if (location == null) {
                _uiState.update {
                    WeatherDashboardUiState.Error
                }
                return@launch
            }

            val weather = withContext(Dispatchers.IO) {
                weatherRepository.getWeather(location.latitude, location.longitude, "imperial")
                    .body()
            }
            if (weather == null) {
                _uiState.update {
                    WeatherDashboardUiState.Error
                }
                return@launch
            } else {
                weather.run {
                    _uiState.update {
                        WeatherDashboardUiState.Success(
                            currentTemperature = temperatureInfo.temperature,
                            currentWeatherDescription = weatherInfoItems[0].weatherDescription,
                            currentFeelsLikeTemperature = temperatureInfo.feelsLikeTemperature
                        )
                    }
                }
            }
        }
    }
}

sealed interface WeatherDashboardUiState {
    data object Loading: WeatherDashboardUiState
    data object RequestLocationPermission: WeatherDashboardUiState
    data object LocationPermissionDeniedPermanently: WeatherDashboardUiState
    data object Error: WeatherDashboardUiState
    data class Success(
        val currentTemperature: Double,
        val currentWeatherDescription: String,
        val currentFeelsLikeTemperature: Double
    ): WeatherDashboardUiState
}