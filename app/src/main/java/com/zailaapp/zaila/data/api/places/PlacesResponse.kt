package com.zailaapp.zaila.data.api.places


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlacesResponse(

    @Json(name = "html_attributions")
    val htmlAttributions: List<Any>?,

    @Json(name = "next_page_token")
    val nextPageToken: String?,

    @Json(name = "results")
    val results: List<PlaceDetail>?,

    @Json(name = "status")
    val status: String?
)