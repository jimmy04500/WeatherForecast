package com.edwardkim.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {
    // Retrieve and display saved weather list from repository
    private val _searchListUiState = MutableStateFlow<SearchLocationListUiState>(SearchLocationListUiState.NoResult)
    val searchListUiState = _searchListUiState.asStateFlow()

    val savedLocations = weatherRepository.weatherData.map { savedWeatherData ->
        savedWeatherData.map { weatherData ->
            SearchLocationListItemInfo(
                name = weatherData.name.name,
                latitude = weatherData.lat,
                longitude = weatherData.lon
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun searchLocation(query: String) {
        viewModelScope.launch {
            val location = withContext(Dispatchers.IO) {
                weatherRepository.searchLocations(query)
            }
            if (location.isNullOrEmpty()) {
                _searchListUiState.update { SearchLocationListUiState.NoResult }
                return@launch
            }

            _searchListUiState.update {
                SearchLocationListUiState.Success(
                    locations = location.map {
                        SearchLocationListItemInfo(
                            name = "${it.name}, " +
                                    (if (it.state == null) "" else "${it.state}, ") +
                                    ", ${it.country}",
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    }
                )
            }
        }
    }
}


sealed interface SearchLocationListUiState {
    data object Loading: SearchLocationListUiState
    data object NoResult: SearchLocationListUiState
    data object Error: SearchLocationListUiState
    data class Success(
        val locations: List<SearchLocationListItemInfo>
    ): SearchLocationListUiState
}

data class SearchLocationListItemInfo(
    val name: String,
    val latitude: Double,
    val longitude: Double
)