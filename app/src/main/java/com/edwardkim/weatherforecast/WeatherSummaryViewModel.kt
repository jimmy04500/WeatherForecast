package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import com.edwardkim.weatherforecast.data.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherSummaryViewModel @Inject constructor(
    private val weatherService: WeatherService
): ViewModel() {
}