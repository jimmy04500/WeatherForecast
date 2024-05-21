package com.edwardkim.weatherforecast.di

import com.edwardkim.weatherforecast.data.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object WeatherRepositoryModule {
    @Provides
    fun provideWeatherRepository(): WeatherRepository {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherRepository::class.java)
    }
}