package com.example.android.kotlincoroutines.main

import com.example.android.kotlincoroutines.util.SkipNetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val service: MainNetwork by lazy {
    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(SkipNetworkInterceptor())
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    retrofit.create(MainNetwork::class.java)
}

fun getNetworkService() = service

interface MainNetwork {
    @GET("next_title.json")
    suspend fun fetchNextTitle(): String
}


