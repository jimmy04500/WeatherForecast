package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.LocationRepository
import com.edwardkim.weatherforecast.data.WeatherForecastItem
import com.edwardkim.weatherforecast.data.WeatherNetwork
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
    private val weatherNetwork: WeatherNetwork,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
): ViewModel() {
//    private val _uiState = MutableStateFlow<WeatherDashboardUiState>(WeatherDashboardUiState.Loading)
//    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = weatherRepository.weatherData.flatMapConcat {
        if (it.isEmpty()) {
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

    fun onLocationPermissionGranted() {
        updateWeather()
    }

    fun onLocationPermissionDeniedPermanently() {
//        _uiState.update {
//            WeatherDashboardUiState.LocationPermissionDeniedPermanently
//        }
    }

    fun updateWeather() {
        viewModelScope.launch {
            // TODO cache / store location
//            _uiState.update {
//                WeatherDashboardUiState.Loading
//            }

            if (!locationRepository.hasLocationPermission()) {
//                _uiState.update {
//                    WeatherDashboardUiState.RequestLocationPermission
//                }
                return@launch
            }

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

//            val currentWeather = async(Dispatchers.IO) {
//                weatherNetwork.getCurrentWeather(location.latitude, location.longitude, "imperial")
//                    .body()
//            }
//            val forecastWeather = async(Dispatchers.IO) {
//                weatherNetwork.get5Day3HourForecast(location.latitude, location.longitude, "imperial")
//                    .body()
//            }
//            val currentLocation = async(Dispatchers.IO) {
//                weatherNetwork.getLocations(location.latitude, location.longitude)
//                    .body()
//            }
//
//            val currentWeatherResponse = currentWeather.await()
//            val forecastWeatherResponse = forecastWeather.await()
//            val currentLocationResponse = currentLocation.await()
//            if (currentWeatherResponse == null || forecastWeatherResponse == null || currentLocationResponse == null) {
//                _uiState.update {
//                    WeatherDashboardUiState.Error
//                }
//                return@launch
//            } else {
//                _uiState.update {
//                    WeatherDashboardUiState.Success(
//                        locationName = currentLocationResponse[0].name,
//                        temperature = currentWeatherResponse.temperatureInfo.temperature,
//                        weatherDescription = currentWeatherResponse.weatherInfoItems[0].weatherDescription,
//                        feelsLikeTemperature = currentWeatherResponse.temperatureInfo.feelsLikeTemperature,
//                        weatherForecastItems = forecastWeatherResponse.forecastInfoItems.map {
//                            WeatherForecastItem(
//                                time = it.timestamp,
//                                temperature = it.temperatureInfo.temperature
//                            )
//                        }
//                    )
//                }
//            }
        }
    }
}

sealed interface WeatherDashboardUiState {
    data object Loading: WeatherDashboardUiState
    data object RequestLocationPermission: WeatherDashboardUiState
    data object LocationPermissionDeniedPermanently: WeatherDashboardUiState
    data object Error: WeatherDashboardUiState
    data class Success(
        val locationName: String,
        val temperature: Double,
        val weatherDescription: String,
        val feelsLikeTemperature: Double,
        val weatherForecastItems: List<WeatherForecastItem>
    ): WeatherDashboardUiState
}