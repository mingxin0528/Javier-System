package com.hwer.app.api

import com.google.gson.GsonBuilder
import com.hwer.app.config.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Api {

    private val okHttpClient = OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Access-Control-Allow-Origin", "*")
                .addHeader("Access-Control-Allow-Headers", "X-Requested-With")
                .addHeader("Vary", "Accept-Encoding")
                .build()
            it.proceed(request)
        }
        .build()
    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constant.API_BASE_URL)
//        .baseUrl("http://192.168.31.93:8045/hwer/common/")
        .build()
    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}