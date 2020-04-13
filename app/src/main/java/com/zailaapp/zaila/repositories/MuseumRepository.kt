package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.model.responses.MuseumResponse

interface MuseumRepository {

    suspend fun getMuseumList(city: String): List<MuseumResponse>

    suspend fun getMuseum(museumId: Int): MuseumResponse

}