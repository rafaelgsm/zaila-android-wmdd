package com.zailaapp.zaila.data.api.zaila

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.zailaapp.zaila.BuildConfig
import com.zailaapp.zaila.utils.PreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object NetworkManager {

    fun buildApi(): ZailaApi {

        val TIMEOUT = 60L

        val logInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        } as Interceptor

        val headerInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()


            if (PreferencesManager.isLogged()) {

                //Add header for access token:
                requestBuilder
                    .header(
                        "Authorization",
                        "Bearer " + PreferencesManager.getToken()
                    )
            }


            chain.proceed(requestBuilder.build())
        }


        val client = OkHttpClient
            .Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

            //Converts json responses, to objects:
            .addConverterFactory(MoshiConverterFactory.create(moshi))

            //Converts objects to json requests:
            .addConverterFactory(UnitConverterFactory)

            .build()
            .create(ZailaApi::class.java)
    }
}

/**
 * Converter to make the responses return non-existing fields as NULL
 */
object UnitConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == Unit::class.java) UnitConverter else null
    }

    private object UnitConverter : Converter<ResponseBody, Unit> {
        override fun convert(value: ResponseBody) {
            value.close()
        }
    }
}