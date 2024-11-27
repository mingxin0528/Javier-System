package com.hwer.app.api

import com.hwer.app.entity.User
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("user/register")
    suspend fun register(@Body body: RequestBody): Result<User>

    @POST("user/login")
    suspend fun login(@Body body: RequestBody): Result<User>

    @POST("user/bindKey")
    fun bindKey(@Body user: User): Result<User>

    @POST("user/getByUsername")
    suspend fun getByUsername(@Body requestBody: RequestBody): Result<User>

    @POST("user/updateAllow")
    suspend fun updateAllow(@Body requestBody: RequestBody): Result<User>

    @POST("user/updateKeys")
    suspend fun updateKeys(@Body requestBody: RequestBody): Result<User>

    @POST("user/destroyAccount")
    suspend fun destroyAccount(@Body requestBody: RequestBody): Result<User>

    @POST("user/hideSecret")
    suspend fun hideSecret(@Body requestBody: RequestBody): Result<User>
}