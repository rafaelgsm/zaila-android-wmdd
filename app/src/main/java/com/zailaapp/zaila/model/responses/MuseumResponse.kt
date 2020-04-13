package com.zailaapp.zaila.model.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MuseumResponse(
    @Json(name = "museum") val museum: Museum? = null
)

@JsonClass(generateAdapter = true)
data class Museum(
    @Json(name = "museumId") val museumId: Int? = null,

    @Json(name = "name") var name: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "categoryId") val categoryId: Int? = null,
    @Json(name = "imageURL") val imageURL: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "province") val province: String? = null,
    @Json(name = "zipcode") val zipcode: String? = null,

    @Json(name = "museum_category") val museum_category: MuseumCategory? = null,
    @Json(name = "exhibitionsList") val exhibitionsList: List<Exhibition>? = null,

    @Json(name = "openingHours") val openingHours: List<OpeningHours>? = null

) {
    var isOpen: Boolean = true

    companion object {
        fun mock(): Museum {
            return Museum(
                0,
                "Vancouver Art Gallery",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                0,
                "",
                "750 Hornby St",
                "Vancouver",
                "BC",
                "123123"
            )
        }
    }
}