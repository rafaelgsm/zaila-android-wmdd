package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.model.responses.ArtworkResponse

class ArtworkRepositoryImpl(private val api: ZailaApi) : ArtworkRepository {

    override suspend fun getArtwork(sensorId: String): Artwork {

        var response = Artwork()

        val result = api.getArtwork(sensorId).await()

        if (result.data?.artwork != null) {
            response = result.data.artwork
        }

        return response
    }

    override suspend fun getArtwork(): List<ArtworkResponse> {
        var response: List<ArtworkResponse> = listOf()


        val result = api.getArtworkList().await()

        if (result.data != null) {
            response = result.data
        }

        return response
    }
}