package com.hwer.app.ui.page

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hwer.app.R
import com.hwer.app.entity.User
import com.hwer.app.ui.component.Comp
import com.hwer.app.ui.theme.Bg
import com.hwer.app.ui.theme.Default
import com.hwer.app.ui.theme.Main
import com.hwer.app.ui.theme.MainDisabled
import com.hwer.app.vm.UserUiState
import com.hwer.app.vm.UserViewModel
import kotlinx.coroutines.launch

object KeysPage {
    @Composable
    fun KeySet(
        ctx: Context,
        nav: NavHostController,
        userViewModel: UserViewModel,
        modifier: Modifier = Modifier
    ) {
        val user = remember { mutableStateOf(User("", "")) }
        val apiKeyErr = remember { mutableStateOf(false) }
        val secretKeyErr = remember { mutableStateOf(false) }

        val apiKeyErrMsg = remember { mutableStateOf("") }
        val secretKeyErrMsg = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        val flag = remember { mutableStateOf(false) }
        user.value.username = userViewModel.username.value
        user.value.apiKey = userViewModel.apiKey.value
        user.value.secretKey = userViewModel.secretKey.value
        Box(
            modifier
                .fillMaxSize()
                .background(color = Bg)
                .padding(32.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.set_key),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    textAlign = TextAlign.Center,
                    color = Main,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = user.value.apiKey.toString(),
                    isError = apiKeyErr.value,
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedTextColor = Main,
                        unfocusedTextColor = Default,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    onValueChange = {
                        user.value.apiKey = it
                        apiKeyErr.value = false
                    },
                    label = { Text(stringResource(R.string.label_api_key)) })
                if (apiKeyErr.value) {
                    Text(
                        text = apiKeyErrMsg.value,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    value = user.value.secretKey.toString(),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedTextColor = Main,
                        unfocusedTextColor = Default,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    onValueChange = {
                        user.value.secretKey = it
                        secretKeyErr.value = false
                    },
                    label = { Text(stringResource(R.string.label_secret_key)) }
                )
                if (secretKeyErr.value) {
                    Text(
                        text = secretKeyErrMsg.value,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Button(
                    onClick = {
                        if (user.value.apiKey?.trim()?.isEmpty() == true) {
                            apiKeyErr.value = true
                            apiKeyErrMsg.value = "请输入 API KEY"
                            return@Button
                        }
                        if (user.value.secretKey?.trim()?.isEmpty() == true) {
                            secretKeyErr.value = true
                            secretKeyErrMsg.value = "请输入 SECRET KEY"
                            return@Button
                        }
                        scope.launch {
                            flag.value = true
                            userViewModel.updateKeys(user.value)
                        }
                    },
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Main,
                        disabledContentColor = MainDisabled,
                        disabledContainerColor = MainDisabled
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 128.dp)
                        .border(
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(2.dp, Main)
                        )
                ) {
                    Text(stringResource(R.string.confirm), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (userViewModel.userUiState == UserUiState.Loading) {
            Comp.Loading()
        } else if (userViewModel.userUiState is UserUiState.Success && flag.value) {
            flag.value = false
            val result = (userViewModel.userUiState as UserUiState.Success).result
            Toast.makeText(
                ctx,
                if (result.code == 1) "配置成功" else result.msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}