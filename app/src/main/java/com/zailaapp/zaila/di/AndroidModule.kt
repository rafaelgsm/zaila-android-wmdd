package com.zailaapp.zaila.di

import com.squareup.moshi.Moshi
import org.koin.dsl.module


/**
 * Default utils to be available through the app:
 */
val androidModule = module {

    single {
        Moshi.Builder()
            .build()
    }

}


