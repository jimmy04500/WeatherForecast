package com.edwardkim.weatherforecast.ui.weatherdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edwardkim.weatherforecast.WeatherDetailUiState
import com.edwardkim.weatherforecast.WeatherDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchedWeatherDetail(
    viewModel: WeatherDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        WeatherDetailUiState.Loading -> {
            Loading()
        }
        WeatherDetailUiState.Error -> TODO()
        is WeatherDetailUiState.Success -> {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveLocation()
                            onNavigateBack()
                        }) {
                            Text(text = "Add")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(text = "Weather")
                    }
                )
                WeatherDetail(
                    locationName = uiState.locationName,
                    temperature = uiState.temperature,
                    weatherDescription = uiState.weatherDescription,
                    feelsLikeTemperature = uiState.feelsLikeTemperature,
                    weatherForecastItems = uiState.weatherForecastItems
                )
            }
        }
    }
}