package com.edwardkim.weatherforecast.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.edwardkim.weatherforecast.SavedLocationsData
import com.edwardkim.weatherforecast.data.SavedLocationDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProtoDataStoreModule {
    private const val DATA_STORE_FILE_NAME = "weather_datastore.proto"
    @Provides
    @Singleton
    fun provideProtoDataStore(
        @ApplicationContext context: Context
    ): DataStore<SavedLocationsData> {
        return DataStoreFactory.create(
            serializer = SavedLocationDataSerializer,
            produceFile = { context.dataStoreFile(DATA_STORE_FILE_NAME) }
        )
    }
}