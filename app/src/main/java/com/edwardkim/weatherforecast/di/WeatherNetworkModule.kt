package com.edwardkim.weatherforecast.di

import com.edwardkim.weatherforecast.data.WeatherNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object WeatherNetworkModule {
    @Provides
    fun provideWeatherNetwork(): WeatherNetwork {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherNetwork::class.java)
    }
}