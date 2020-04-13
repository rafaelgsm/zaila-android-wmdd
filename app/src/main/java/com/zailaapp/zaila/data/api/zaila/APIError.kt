package com.zailaapp.zaila.data.api.zaila

import com.squareup.moshi.JsonDataException
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Centralize here the api message parsing for all error responses!
 */
fun Throwable.getApiMessage(): String {

    if (this is HttpException) {
        val error = ""//ErrorUtils.parseError(response())

        if (code() == 404) {
            val text = message()
            return if (text.isEmpty()) ZailaApplication.instance.resources.getString(R.string.error_api_404) else text
        }

        if (code() == 405) {
            return ZailaApplication.instance.resources.getString(R.string.error_api_405)
        }


        if (code() == 500) {
            return ZailaApplication.instance.resources.getString(R.string.error_api_500)
        }

        if (code() == 503) {
            return ZailaApplication.instance.resources.getString(R.string.error_api_500)
        }

        return message()//error.message()
    }

    if (this is UnknownHostException) {
        return ZailaApplication.instance.resources.getString(R.string.tv_connection_retry)
    }

    //422...
    if (this is IllegalStateException) {
        return ZailaApplication.instance.resources.getString(R.string.error_api_illegal_state)
    }

    if (this is JsonDataException) {
        return ZailaApplication.instance.resources.getString(R.string.JsonDataException)
    }

    return ""
}

fun Throwable.getApiCode(): Int {

    if (this is HttpException) {

        return code()
    }

    if (this is UnknownHostException) {
        //...
    }

    //422...
    if (this is IllegalStateException) {
        return 422
    }

    return -1
}