package com.lucaslei.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Base URL for Cloudflare Worker API - configure as needed
    private const val BASE_URL = "https://lucaslei.cyou/api/"

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val cloudflareApi: CloudflareApi = retrofit.create(CloudflareApi::class.java)
}
