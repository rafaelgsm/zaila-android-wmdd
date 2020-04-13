package com.zailaapp.zaila.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zailaapp.zaila.ZailaApplication

object JsonProvider {

    val instance: Gson by lazy {
        Gson()
    }


    inline fun <reified T> toJson(json: T): String =
        instance.toJson(json)

    inline fun <reified T> fromJson(json: String): T =
        instance.fromJson<T>(json, object : TypeToken<T>() {}.type)

    inline fun <reified T> toJson(json: String): String? =
        instance.toJson(json, object : TypeToken<T>() {}.type)

    inline fun <reified T> fromJson(rawRes: Int): T {

        val textFromRaw =
            ZailaApplication.instance.resources.openRawResource(rawRes).bufferedReader()
                .use { it.readText() }

        return instance.fromJson<T>(textFromRaw, object : TypeToken<T>() {}.type)
    }

}



