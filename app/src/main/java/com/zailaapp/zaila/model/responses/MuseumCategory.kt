package com.zailaapp.zaila.model.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MuseumCategory(
    @Json(name = "categoryId") val categoryId: Int? = null,
    @Json(name = "categoryName") val categoryName: String? = null,
    @Json(name = "imageURL") val imageURL: String? = null
)