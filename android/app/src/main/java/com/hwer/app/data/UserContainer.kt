package com.hwer.app.data

import com.hwer.app.api.UserService
import com.hwer.app.config.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface UserContainer {
    val userRepository: UserRepository
}

class DefaultUserContainer : UserContainer {
    private val okHttpClient = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
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
        .build()
    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userService)
    }
}