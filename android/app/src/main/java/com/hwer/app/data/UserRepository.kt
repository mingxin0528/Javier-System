package com.hwer.app.data

import com.hwer.app.api.Result
import com.hwer.app.api.UserService
import com.hwer.app.entity.User
import okhttp3.RequestBody

interface UserRepository {
    suspend fun register(body: RequestBody): Result<User>
    suspend fun login(requestBody: RequestBody): Result<User>
    suspend fun getByUsername(requestBody: RequestBody): Result<User>
    suspend fun updateAllow(requestBody: RequestBody): Result<User>
    suspend fun updateKeys(requestBody: RequestBody): Result<User>
    suspend fun destroyAccount(requestBody: RequestBody): Result<User>
    suspend fun hideSecret(requestBody: RequestBody): Result<User>
}

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository {

    override suspend fun register(body: RequestBody): Result<User> {
        return userService.register(body)
    }

    override suspend fun login(requestBody: RequestBody): Result<User> {
        return userService.login(requestBody)
    }

    override suspend fun getByUsername(requestBody: RequestBody): Result<User> {
        return userService.getByUsername(requestBody)
    }

    override suspend fun updateAllow(requestBody: RequestBody): Result<User> {
        return userService.updateAllow(requestBody)
    }

    override suspend fun updateKeys(requestBody: RequestBody): Result<User> {
        return userService.updateKeys(requestBody)
    }

    override suspend fun destroyAccount(requestBody: RequestBody): Result<User> {
        return userService.destroyAccount(requestBody)
    }

    override suspend fun hideSecret(requestBody: RequestBody): Result<User> {
        return userService.hideSecret(requestBody)
    }
}