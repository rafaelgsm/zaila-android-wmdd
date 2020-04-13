package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.data.api.zaila.ZailaApi
import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.model.requests.UserLoginRequest
import com.zailaapp.zaila.model.responses.LoginResponse

class AuthRepositoryImpl(private val api: ZailaApi) : AuthRepository {
    override suspend fun loginUser(userLoginRequest: UserLoginRequest): LoginResponse {

        val result = api.loginUser(userLoginRequest).await()

        return result
    }

    override suspend fun loginUserGoogle(idToken: String): LoginResponse {
        val result = api.loginUserGoogle("Bearer $idToken").await()
        return result
    }

    override suspend fun registerUserGoogle(idToken: String): UserZaila {
        val result = api.registerUserGoogle("Bearer $idToken").await()
        return result
    }

}