package com.zailaapp.zaila.repositories

import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.model.requests.UserLoginRequest
import com.zailaapp.zaila.model.responses.LoginResponse

interface AuthRepository {

    suspend fun loginUser(userLoginRequest: UserLoginRequest): LoginResponse

    suspend fun loginUserGoogle(idToken: String): LoginResponse

    suspend fun registerUserGoogle(idToken: String): UserZaila

}