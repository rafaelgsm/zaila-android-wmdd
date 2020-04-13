package com.zailaapp.zaila.data.api.zaila

import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.model.requests.UserLoginRequest
import com.zailaapp.zaila.model.responses.ArtworkResponse
import com.zailaapp.zaila.model.responses.LoginResponse
import com.zailaapp.zaila.model.responses.MuseumResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface ZailaApi {

    //region museum

    @GET("api/museum")
    fun getMuseumList(
        @Query("city") city: String
    ): Deferred<APIResponse<List<MuseumResponse>>>

    @GET("api/museum/{id}")
    fun getMuseum(
        @Path("id") id: Int
    ): Deferred<APIResponse<MuseumResponse>>

    //endregion museum

    //region artwork
    @GET("api/artwork")
    fun getArtworkList(): Deferred<APIResponse<List<ArtworkResponse>>>

    @GET("api/artwork")
    fun getArtwork(
        @Query("sensorId") sensorId: String
    ): Deferred<APIResponse<ArtworkResponse>>
    //endregion artwork

    //region user
    @POST("auth/login")
    fun loginUser(
        @Body userLoginRequest: UserLoginRequest
    ): Deferred<LoginResponse>

    @POST("auth/login")
    fun loginUserGoogle(
        @Header("Authorization") idToken: String
    ): Deferred<LoginResponse>

    @POST("auth/registerUser")
    fun registerUserGoogle(
        @Header("Authorization") idToken: String
    ): Deferred<UserZaila>
    //endregion user
}