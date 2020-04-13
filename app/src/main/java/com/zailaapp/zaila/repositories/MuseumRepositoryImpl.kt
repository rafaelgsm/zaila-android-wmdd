package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.responses.MuseumResponse

class MuseumRepositoryImpl(private val api: ZailaApi) : MuseumRepository {

    override suspend fun getMuseumList(city: String): List<MuseumResponse> {

        var response: List<MuseumResponse> = listOf()

        val result = api.getMuseumList(city).await()

        if (result.data != null) {
            response = result.data
        }

        return response
    }

    override suspend fun getMuseum(museumId: Int): MuseumResponse {
        var response: MuseumResponse =
            MuseumResponse()

        val result = api.getMuseum(museumId).await()

        if (result.data != null) {
            response = result.data
        }

        return response
    }
}