package com.zailaapp.zaila.data.api.zaila

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class APIResponse<T>(

    @Json(name = "errorCode")
    val errorCode: Int?,

    @Json(name = "errorMessage")
    val errorMessage: String?,

    @Json(name = "data")
    val data: T?
)