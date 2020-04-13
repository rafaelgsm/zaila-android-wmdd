package com.zailaapp.zaila.di

import com.zailaapp.zaila.repositories.*
import org.koin.dsl.module

val repositoryModule = module {

    /*
        Whenever we do a "by inject()" in our application, we will retrieve a singleton object of
        ArtworkRepository type. The "get(zailaApi)", is the "inject" way of doing it through modules.
    */

//    single<ArtworkRepository> { ArtworkRepositoryImpl(api = get(zailaApi)) }
    single<ArtworkRepository> { ArtworkRepositoryImplMock(api = get(zailaApi)) }

//    single<MuseumRepository> { MuseumRepositoryImpl(api = get(zailaApi)) }
    single<MuseumRepository> { MuseumRepositoryImplMock(api = get(zailaApi)) }

//    single<AuthRepository> { AuthRepositoryImpl(api = get(zailaApi)) }
    single<AuthRepository> { AuthRepositoryImplMock(api = get(zailaApi)) }
}