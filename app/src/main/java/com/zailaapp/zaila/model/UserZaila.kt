package com.zailaapp.zaila.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserZaila(
    @Json(name = "name") val name: String? = null,

    @Json(name = "email") val email: String? = null,
    @Json(name = "password") val password: String? = null,

    @Json(name = "ageGroup") val ageGroup: String? = null,
    @Json(name = "preferredLanguage") val preferredLanguage: String? = null,
    @Json(name = "autoPlayDescription") val autoPlayDescription: String? = null,
    @Json(name = "autoEnrollQuest") val autoEnrollQuest: String? = null,
    @Json(name = "userXP") val userXP: String? = null
)