package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.data.api.zaila.APIResponse
import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.model.responses.ArtworkResponse
import com.zailaapp.zaila.utils.JsonProvider
import kotlinx.coroutines.delay

class ArtworkRepositoryImplMock(private val api: ZailaApi) : ArtworkRepository {

    override suspend fun getArtwork(sensorId: String): Artwork {

        var response = Artwork()

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.artwork_detail_mock)
            .bufferedReader()
            .use { it.readText() }

        val result: APIResponse<ArtworkResponse> = JsonProvider.fromJson(mock)

        if (result.data != null) {
            response = result.data.artwork!!
        }

        delay(400)

        return response
    }

    override suspend fun getArtwork(): List<ArtworkResponse> {
        var response: List<ArtworkResponse> = listOf()

        //...

        return response
    }
}