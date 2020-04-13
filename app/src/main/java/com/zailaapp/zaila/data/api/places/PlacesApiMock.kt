package com.zailaapp.zaila.data.api.places

import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.utils.JsonProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Simple singleton to request nearby places.
 */
class PlacesApiMock : PlacesApi {

    override suspend fun getNearbyMuseums(lat: String, lng: String): List<PlaceDetail> {

        return suspendCoroutine { continuation ->

            val mock = ZailaApplication.instance.resources.openRawResource(R.raw.placesmock)
                .bufferedReader()
                .use { it.readText() }
            val placesResponse: PlacesResponse = JsonProvider.fromJson(mock)

            val result = placesResponse.results ?: listOf()

            continuation.resume(result)


        } //end suspendCoroutine
    }

}