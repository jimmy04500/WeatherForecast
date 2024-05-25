package com.edwardkim.weatherforecast.di

import androidx.datastore.core.DataStore
import com.edwardkim.weatherforecast.SavedLocationsData
import com.edwardkim.weatherforecast.data.WeatherNetwork
import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object WeatherRepositoryModule {
    @Provides
    fun provideWeatherRepository(
        weatherNetwork: WeatherNetwork,
        weatherDataStore: DataStore<SavedLocationsData>
    ): WeatherRepository {
        return WeatherRepository(weatherNetwork, weatherDataStore)
    }
}