package com.hwer.app.vm

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import com.hwer.app.HwerApplication
import com.hwer.app.api.Result
import com.hwer.app.data.UserRepository
import com.hwer.app.entity.User
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

sealed interface UserUiState {
    data class Success(val result: Result<User>) : UserUiState
    data object Loading : UserUiState
    data object Error : UserUiState
    data object Default : UserUiState
}

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    var userId = MutableLiveData<Int>()
    var apiKey = MutableLiveData<String>()
    var secretKey = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    var userUiState: UserUiState by mutableStateOf(UserUiState.Default)
        private set

    private fun requestBody(user: User): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            Gson().toJson(user)
        )
    }

    fun register(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.register(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun login(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.login(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun getByUsername(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.getByUsername(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun updateAllow(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.updateAllow(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun updateKeys(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.updateKeys(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun destroyAccount(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.destroyAccount(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }

    fun hideSecret(user: User) {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                UserUiState.Success(userRepository.hideSecret(requestBody(user)))
            } catch (e: Exception) {
                e.printStackTrace()
                UserUiState.Error
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HwerApplication)
                val userRepo = application.container.userRepository
                UserViewModel(userRepository = userRepo)
            }
        }
    }
}