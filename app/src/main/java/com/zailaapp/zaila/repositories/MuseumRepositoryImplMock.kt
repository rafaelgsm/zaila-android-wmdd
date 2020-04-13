package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.data.api.zaila.APIResponse
import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.responses.MuseumResponse
import com.zailaapp.zaila.utils.JsonProvider
import kotlinx.coroutines.delay

class MuseumRepositoryImplMock(private val api: ZailaApi) : MuseumRepository {

    override suspend fun getMuseumList(city: String): List<MuseumResponse> {

        var response: List<MuseumResponse> = listOf()

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.museum_mock)
            .bufferedReader()
            .use { it.readText() }

        val result: APIResponse<List<MuseumResponse>> = JsonProvider.fromJson(mock)

        if (result.data != null) {
            response = result.data
        }

        delay(800)

        return response
    }

    override suspend fun getMuseum(museumId: Int): MuseumResponse {

        var response: MuseumResponse = MuseumResponse()

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.museum_mock_detail)
            .bufferedReader()
            .use { it.readText() }

        val result: APIResponse<MuseumResponse> = JsonProvider.fromJson(mock)

        if (result.data != null) {
            response = result.data
        }

//        delay(500)

        return response
    }
}