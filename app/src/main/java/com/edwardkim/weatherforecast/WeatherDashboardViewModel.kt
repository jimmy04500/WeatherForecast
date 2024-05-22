package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.LocationRepository
import com.edwardkim.weatherforecast.data.WeatherRepository
import com.edwardkim.weatherforecast.ui.WeatherForecastItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

            val currentWeather = async(Dispatchers.IO) {
                weatherRepository.getCurrentWeather(location.latitude, location.longitude, "imperial")
                    .body()
            }
            val forecastWeather = async(Dispatchers.IO) {
                weatherRepository.get5Day3HourForecast(location.latitude, location.longitude, "imperial")
                    .body()
            }

            val currentWeatherResponse = currentWeather.await()
            val forecastWeatherResponse = forecastWeather.await()
            if (currentWeatherResponse == null || forecastWeatherResponse == null) {
                _uiState.update {
                    WeatherDashboardUiState.Error
                }
                return@launch
            } else {
                _uiState.update {
                    WeatherDashboardUiState.Success(
                        currentTemperature = currentWeatherResponse.temperatureInfo.temperature,
                        currentWeatherDescription = currentWeatherResponse.weatherInfoItems[0].weatherDescription,
                        currentFeelsLikeTemperature = currentWeatherResponse.temperatureInfo.feelsLikeTemperature,
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

sealed interface WeatherDashboardUiState {
    data object Loading: WeatherDashboardUiState
    data object RequestLocationPermission: WeatherDashboardUiState
    data object LocationPermissionDeniedPermanently: WeatherDashboardUiState
    data object Error: WeatherDashboardUiState
    data class Success(
        val currentTemperature: Double,
        val currentWeatherDescription: String,
        val currentFeelsLikeTemperature: Double,
        val weatherForecastItems: List<WeatherForecastItem>
    ): WeatherDashboardUiState
}