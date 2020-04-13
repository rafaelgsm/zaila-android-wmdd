package com.zailaapp.zaila

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.zailaapp.zaila.di.androidModule
import com.zailaapp.zaila.di.networkModule
import com.zailaapp.zaila.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ZailaApplication : Application() {

    companion object {
        lateinit var instance: ZailaApplication

        var isShortPhoneHeight: Boolean = false
    }

    init {

        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        Bugsnag.init(this)

        setupKoin()

        /////////////////////////////////////////////////////////////////////////////////////
        //region UNIVERSAL IMAGE LOADER
        /////////////////////////////////////////////////////////////////////////////////////

        val options: DisplayImageOptions? = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .build()// default

        // Create global configuration and initialize ImageLoader with this config
        val config: ImageLoaderConfiguration = ImageLoaderConfiguration.Builder(this)
            .defaultDisplayImageOptions(options)
            .build()
        ImageLoader.getInstance().init(config)

        //endregion UNIVERSAL IMAGE LOADER
        /////////////////////////////////////////////////////////////////////////////////////
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@ZailaApplication)
            modules(
                listOf(
                    androidModule,
                    networkModule,
                    repositoryModule
                )
            )
        }
    }
}