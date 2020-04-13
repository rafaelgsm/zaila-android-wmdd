package com.zailaapp.zaila.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zailaapp.zaila.model.UserZaila

@JsonClass(generateAdapter = true)
data class UserRequest(
    @Json(name = "user") val user: UserZaila? = null
)