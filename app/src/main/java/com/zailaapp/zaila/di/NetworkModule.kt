package com.zailaapp.zaila.di

import com.zailaapp.zaila.data.api.places.PlacesApi
import com.zailaapp.zaila.data.api.places.PlacesApiImpl
import com.zailaapp.zaila.data.api.places.PlacesApiMock
import com.zailaapp.zaila.data.api.zaila.NetworkManager
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

//Creates the string qualifier for use it as a reference for injections.
val zailaApi = StringQualifier("zailaApi")

val networkModule = module {

    //A single instance will be available. @see RepositoryModule.kt
    single(zailaApi) { NetworkManager.buildApi() }

    single<PlacesApi> { PlacesApiImpl() }
//    single<PlacesApi> { PlacesApiMock() }


    // Define a singleton of type HttpClient
    single { NetworkManager.buildApi() }
}


