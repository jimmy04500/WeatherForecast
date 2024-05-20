package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDashboardViewModel @Inject constructor(
    private val weatherService: WeatherService
): ViewModel() {
    private val _currentTemperature = MutableStateFlow(0.0)
    val currentTemperature = _currentTemperature.asStateFlow()

    private val _currentWeatherDescription = MutableStateFlow("")
    val currentWeatherDescription = _currentWeatherDescription.asStateFlow()

    private val _currentFeelsLikeTemperature = MutableStateFlow(0.0)
    val currentFeelsLikeTemperature = _currentFeelsLikeTemperature.asStateFlow()

    fun updateWeather() {
        viewModelScope.launch {
            val result = weatherService.getWeather(41.878113, -87.629799, "imperial").body()
            result?.run {
                _currentTemperature.update {
                    temperatureInfo.temperature
                }
                _currentWeatherDescription.update {
                    weatherInfoItems[0].weatherDescription
                }
                _currentFeelsLikeTemperature.update {
                    temperatureInfo.feelsLikeTemperature
                }
            }
        }
    }
}