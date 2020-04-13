package com.zailaapp.zaila.model.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zailaapp.zaila.model.UserZaila

@JsonClass(generateAdapter = true)
data class ArtworkResponse(
    @Json(name = "artwork") val artwork: Artwork? = null,
    @Json(name = "user") val user: UserZaila? = null
)

@JsonClass(generateAdapter = true)
data class Artwork(
    @Json(name = "title") val title: String? = null,
    @Json(name = "imageURL") var imageURL: String? = null,
    @Json(name = "artistName") val artistName: String? = null,
    @Json(name = "media") val media: String? = null,
    @Json(name = "year") val year: String? = null,
    @Json(name = "exhibitionId") val exhibitionId: Int? = null,
    @Json(name = "sensorId") val sensorId: String? = null,
    @Json(name = "artworkId") val artworkId: Int? = null,

    @Json(name = "artworkDetails") val artworkDetails: List<ArtworkDetails>? = null
)

@JsonClass(generateAdapter = true)
data class ArtworkDetails(
    @Json(name = "artworkDetailsId") val artworkDetailsId: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "languageCode") val languageCode: String? = null
)