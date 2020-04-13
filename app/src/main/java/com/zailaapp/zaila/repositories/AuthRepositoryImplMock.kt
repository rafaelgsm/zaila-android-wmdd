package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.model.requests.UserLoginRequest
import com.zailaapp.zaila.model.responses.LoginResponse
import com.zailaapp.zaila.utils.JsonProvider
import kotlinx.coroutines.delay

class AuthRepositoryImplMock(private val api: ZailaApi) : AuthRepository {
    override suspend fun loginUser(userLoginRequest: UserLoginRequest): LoginResponse {

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.login_mock)
            .bufferedReader()
            .use { it.readText() }

        delay(800)

        return JsonProvider.fromJson(mock)
    }

    override suspend fun loginUserGoogle(idToken: String): LoginResponse {

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.login_mock)
            .bufferedReader()
            .use { it.readText() }

        delay(800)

        return JsonProvider.fromJson(mock)
    }

    override suspend fun registerUserGoogle(idToken: String): UserZaila {
        val result = api.registerUserGoogle("Bearer $idToken").await()

        delay(800)

        return result
    }

}