package com.edwardkim.weatherforecast.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.edwardkim.weatherforecast.SavedLocationsData
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SavedLocationDataSerializer: Serializer<SavedLocationsData> {
    override val defaultValue: SavedLocationsData = SavedLocationsData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SavedLocationsData {
        try {
            return SavedLocationsData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SavedLocationsData, output: OutputStream) = t.writeTo(output)
}
