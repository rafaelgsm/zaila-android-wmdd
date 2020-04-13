package com.zailaapp.zaila.data.api.places

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.utils.JsonProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Simple singleton to request nearby places.
 */
class PlacesApiImpl : PlacesApi {

    override suspend fun getNearbyMuseums(lat: String, lng: String): List<PlaceDetail> {

        return suspendCoroutine { continuation ->

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(ZailaApplication.instance)

            val url = ""
//                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${BuildConfig.PLACES_KEY}&location=$lat,$lng&radius=5000&type=museum"

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->

                    val placesResponse: PlacesResponse = JsonProvider.fromJson(response)

                    val result = placesResponse.results ?: listOf()

                    continuation.resume(result)

                },
                Response.ErrorListener {
                    //textView.text = "That didn't work!"
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

        } //end suspendCoroutine
    }

}
