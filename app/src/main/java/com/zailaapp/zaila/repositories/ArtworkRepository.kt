package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.model.responses.ArtworkResponse

interface ArtworkRepository {

    suspend fun getArtwork(sensorId: String): Artwork

    suspend fun getArtwork(): List<ArtworkResponse>

}