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
import com.hwer.app.config.Route
import com.hwer.app.entity.User
import com.hwer.app.ui.component.Comp
import com.hwer.app.ui.theme.Bg
import com.hwer.app.ui.theme.Default
import com.hwer.app.ui.theme.Main
import com.hwer.app.ui.theme.MainDisabled
import com.hwer.app.vm.UserUiState
import com.hwer.app.vm.UserViewModel
import kotlinx.coroutines.launch

object RegisterPage {
    @Composable
    fun Register(
        ctx: Context,
        nav: NavHostController,
        userViewModel: UserViewModel,
        modifier: Modifier = Modifier
    ) {
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val passwordConfirm = remember { mutableStateOf("") }
        val code = remember { mutableStateOf("") }

        val usernameErr = remember { mutableStateOf(false) }
        val passwordErr = remember { mutableStateOf(false) }
        val passwordConfirmErr = remember { mutableStateOf(false) }

        val usernameErrMsg = remember { mutableStateOf("") }
        val passwordErrMsg = remember { mutableStateOf("") }
        val passwordConfirmErrMsg = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        val flag = remember { mutableStateOf(false) }
        Box(
            modifier
                .fillMaxSize()
        ) {
            Box(
                modifier
                    .fillMaxSize()
                    .background(color = Bg)
                    .padding(32.dp)
            ) {

                Column {
                    Text(
                        text = stringResource(R.string.register),
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
                        value = username.value,
                        isError = usernameErr.value,
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            focusedTextColor = Main,
                            unfocusedTextColor = Default,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                        ),
                        onValueChange = {
                            username.value = it
                            usernameErr.value = false
                        },
                        label = { Text(stringResource(R.string.label_username)) })
                    if (usernameErr.value) {
                        Text(
                            text = usernameErrMsg.value,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password.value,
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            focusedTextColor = Main,
                            unfocusedTextColor = Default,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                        ),
                        onValueChange = {
                            password.value = it
                            passwordErr.value = false
                        },
                        label = { Text(stringResource(R.string.label_password)) },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (passwordErr.value) {
                        Text(
                            text = passwordErrMsg.value,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = passwordConfirm.value,
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            focusedTextColor = Main,
                            unfocusedTextColor = Default,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                        ),
                        onValueChange = {
                            passwordConfirm.value = it
                            passwordConfirmErr.value = false
                        },
                        label = { Text(stringResource(R.string.label_password_confirm)) },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (passwordConfirmErr.value) {
                        Text(
                            text = passwordConfirmErrMsg.value,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = code.value,
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            focusedTextColor = Main,
                            unfocusedTextColor = Default,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                        ),
                        onValueChange = {
                            code.value = it
                        },
                        label = { Text(stringResource(R.string.label_recommend_code)) },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Text),
                    )
                    Button(
                        onClick = {
                            if (username.value.trim().isEmpty()) {
                                usernameErr.value = true
                                usernameErrMsg.value = "请填写用户名"
                                return@Button
                            }
                            if (password.value.trim().isEmpty()) {
                                passwordErr.value = true
                                passwordErrMsg.value = "请填写密码"
                                return@Button
                            }
                            if (password.value != passwordConfirm.value) {
                                passwordErr.value = true
                                passwordErrMsg.value = "两次密码不一致"
                                return@Button
                            }
                            scope.launch {
                                flag.value = true
                                userViewModel.register(
                                    User(
                                        username = username.value,
                                        password = password.value,
                                        usedCode = code.value
                                    )
                                )
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
                        Text(stringResource(R.string.register), fontWeight = FontWeight.Bold)
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
                    if (result.code == 1) "注册成功" else result.msg,
                    Toast.LENGTH_SHORT
                ).show()
                if (result.code == 1) {
                    userViewModel.username.value=username.value
                    userViewModel.password.value=password.value
                    nav.popBackStack()
                    nav.navigate(Route.PAGE_LOGIN)
                }
            }
        }
    }
}