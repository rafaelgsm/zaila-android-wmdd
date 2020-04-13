package com.zailaapp.zaila.data.api.places

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceDetail(

    @Json(name = "icon")
    val icon: String?,

    @Json(name = "id")
    val id: String?,

    @Json(name = "place_id")
    val place_id: String?,

    @Json(name = "name")
    val name: String?,

    @Json(name = "photos")
    val photos: List<PlacePhoto>?,

    @Json(name = "formatted_address")
    val formattedAddress: String?,

    @Json(name = "reference")
    val reference: String?,

    var isOpen: Boolean = true
)


@JsonClass(generateAdapter = true)
data class PlacePhoto(

    @Json(name = "width")
    val width: Int?,

    @Json(name = "height")
    val height: Int?,

    @Json(name = "html_attributions")
    val htmlAttributions: List<Any>?,

    @Json(name = "photo_reference")
    val photo_reference: String?

)