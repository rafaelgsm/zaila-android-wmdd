package com.zailaapp.zaila.model.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exhibition(

    @Json(name = "museumId") val museumId: Int? = null,

    @Json(name = "exhibitionId") val exhibitionId: Int? = null,

    @Json(name = "name") val name: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "imageURL") val imageURL: String? = null,
    @Json(name = "startDate") val startDate: String? = null,
    @Json(name = "endDate") val endDate: String? = null,

    @Json(name = "categoryId") val categoryId: Int? = null,
    @Json(name = "completionBadgeId") val completionBadgeId: Int? = null,
    @Json(name = "completionXP") val completionXP: Int? = null
) {
    var isOpen: Boolean = true

    companion object {
        fun mock(): Exhibition {
            return Exhibition(
                0,
                0,
                "Rapture, Rhythm and The Tree of Life",
                "Emily Carr and Her Female Contemporaries focuses on artwork from the first half of the twentieth century by women artists based in British Columbia.",
                null,
                "2019-12-07T00:00:00.000Z",
                "2019-12-07T00:00:00.000Z"
            )
        }
    }
}