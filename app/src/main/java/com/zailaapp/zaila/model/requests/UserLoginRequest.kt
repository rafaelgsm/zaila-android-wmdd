package com.zailaapp.zaila.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    @Json(name = "email") val email: String? = null,
    @Json(name = "password") val password: String? = null
)