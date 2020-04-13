package com.zailaapp.zaila.model.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class OpeningHours(
    @Json(name = "openingHoursId") val openingHoursId: Int? = null,
    @Json(name = "museumId") val museumId: Int? = null,
    @Json(name = "day") val day: String? = null,
    @Json(name = "startTime") val startTime: String? = null,
    @Json(name = "endTime") val endTime: String? = null
)