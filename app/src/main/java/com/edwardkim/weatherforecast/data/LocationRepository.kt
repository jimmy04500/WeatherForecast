package com.edwardkim.weatherforecast.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class LocationRepository @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val location = fusedLocationClient.lastLocation.await()
        return if (location != null) {
            Location(location.latitude, location.longitude)
        } else {
            null
        }
    }
}

data class Location(
    val latitude: Double,
    val longitude: Double
)