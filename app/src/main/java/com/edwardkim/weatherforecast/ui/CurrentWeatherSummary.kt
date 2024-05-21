package com.edwardkim.weatherforecast.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edwardkim.weatherforecast.WeatherDashboardUiState
import com.edwardkim.weatherforecast.WeatherDashboardViewModel

@Composable
fun CurrentWeatherSummary(
    viewModel: WeatherDashboardViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        WeatherDashboardUiState.Loading -> {
            Loading()
        }
        WeatherDashboardUiState.RequestLocationPermission -> {
            LocationPermissionRequester(
                onPermissionGranted = {
                    viewModel.onLocationPermissionGranted()
                },
                onPermissionDeniedPermanently = {
                    viewModel.onLocationPermissionDeniedPermanently()
                }
            )
        }
        WeatherDashboardUiState.LocationPermissionDeniedPermanently -> {
            LocationPermissionDenied()
        }
        WeatherDashboardUiState.Error -> TODO()
        is WeatherDashboardUiState.Success -> {
            CurrentWeatherSummary(
                city = "Chicago",
                temperature = uiState.currentTemperature,
                weatherDescription = uiState.currentWeatherDescription,
                feelsLikeTemp = uiState.currentFeelsLikeTemperature,
                modifier = modifier
            )
        }
    }
}

@Composable
fun Loading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
fun LocationPermissionRequester(
    onPermissionGranted: () -> Unit = {},
    onPermissionDeniedPermanently: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAlertDialogOpen = remember { mutableStateOf(false) }
    when {
        isAlertDialogOpen.value -> {
            AllowLocationDialog(onDismiss = { isAlertDialogOpen.value = false })
        }
    }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        when {
            isGranted -> {
                onPermissionGranted()
            }
            shouldShowRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                isAlertDialogOpen.value = true
            }
            else -> {
                onPermissionDeniedPermanently()
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    LocationPermissionRequest(
        onAllowLocation = {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        },
        modifier = modifier
    )
}

fun shouldShowRationale(context: Context, permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
}

@Composable
fun LocationPermissionRequest(
    onAllowLocation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {
        Text(
            text = "Location permission is required to show weather at your location",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(16.dp))
        FilledTonalButton(onClick = onAllowLocation) {
            Text(text = "Allow Location Access")
        }
    }
}

@Composable
fun LocationPermissionDenied() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Please navigate to settings and grant location permission.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp))
        FilledTonalButton(onClick = {
        }) {
            Text(text = "Go To Settings")
        }
    }
}

@Composable
fun AllowLocationDialog(
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Confirm")
            }
        },
        text = {
            Text(text = "Please allow location permission to show weather for your location.")
        },
    )
}

@Composable
fun CurrentWeatherSummary(
    city: String,
    temperature: Double,
    weatherDescription: String,
    feelsLikeTemp: Double,
    modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = city,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = weatherDescription,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Feels like $feelsLikeTemp°",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrentWeatherSummary() {
    CurrentWeatherSummary(
        city = "San Francisco",
        temperature = 72.0,
        weatherDescription = "Sunny",
        feelsLikeTemp = 80.0)
}

@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    Loading()
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationPermissionRequest() {
    LocationPermissionRequest()
}

@Preview(showBackground = true)
@Composable
fun PreviewAllowLocationDialog() {
    AllowLocationDialog()
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationPermissionDenied() {
    LocationPermissionDenied()
}
