package com.hwer.app.ui.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hwer.app.R
import com.hwer.app.config.Route
import com.hwer.app.data.clear
import com.hwer.app.data.getUsername
import com.hwer.app.entity.User
import com.hwer.app.ui.component.Comp
import com.hwer.app.ui.theme.Bg
import com.hwer.app.ui.theme.MainDisabled
import com.hwer.app.vm.UserUiState
import com.hwer.app.vm.UserViewModel
import java.util.Locale

object MainPage {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Main(
        ctx: Context,
        nav: NavHostController,
        userViewModel: UserViewModel,
    ) {
        val username = getUsername(ctx)
        if (username.isNullOrEmpty()) {
            LaunchedEffect("backMain") {
                nav.navigate(Route.PAGE_WELCOME) {
                    popUpTo(Route.PAGE_MAIN) { inclusive = true }
                }
            }
            return
        }
        val user = remember { mutableStateOf(User(username, "")) }
        val showDialog = remember { mutableIntStateOf(0) }
        val showDestroyAccountDialog = remember { mutableStateOf(false) }
        val flag = remember { mutableStateOf(true) }
        val refreshed = remember { mutableStateOf(true) }
        val allowStrategy = remember { mutableStateOf("关") }
        if (refreshed.value) {
            userViewModel.getByUsername(user.value)
            refreshed.value = false
            flag.value = true
        }

        val state = rememberPullToRefreshState()
        if (state.isRefreshing) {
            LaunchedEffect(true) {
                flag.value = true
                userViewModel.getByUsername(user.value)
                state.endRefresh()
            }
        }
        Box(
            modifier = Modifier
                .nestedScroll(state.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Bg)
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            start = 32.dp,
                            end = 32.dp,
                            top = 64.dp,
                        )
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = username,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${stringResource(R.string.label_allow_strategy)}:${allowStrategy.value}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainDisabled, modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            colors = SwitchDefaults.colors().copy(
                                uncheckedIconColor = com.hwer.app.ui.theme.Main,
                                uncheckedThumbColor = com.hwer.app.ui.theme.Main,
                                uncheckedBorderColor = com.hwer.app.ui.theme.Main,
                                uncheckedTrackColor = Color.Transparent,
                                checkedThumbColor = Bg,
                                checkedIconColor = com.hwer.app.ui.theme.Main,
                                checkedTrackColor = com.hwer.app.ui.theme.Main,
                                checkedBorderColor = com.hwer.app.ui.theme.Main
                            ), checked = user.value.allow == 1, onCheckedChange = {
                                user.value.allow = if (it) 1 else 0
                                userViewModel.updateAllow(user.value)
                                flag.value = true
                            })
                    }
                }
                Row(
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 32.dp,
                        top = 128.dp,
                        bottom = 32.dp
                    ), verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = stringResource(R.string.label_binance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainDisabled
                    )
                    Column {
                        if (user.value.binanceList.isEmpty()) {
                            Text(
                                text = "nu",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.hwer.app.ui.theme.Main,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                        }
                        for (item in user.value.binanceList) {
                            if (item.asset.isEmpty() || item.asset.uppercase() != "USDT") {
                                continue
                            }
                            Text(
                                text = "${item.asset}: ${item.availableBalance.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.hwer.app.ui.theme.Main,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }


                Row(
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 32.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_self_code),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainDisabled
                    )
                    Text(
                        text = user.value.selfCode ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.hwer.app.ui.theme.Main,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(
                                1.dp,
                                Color.LightGray, shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                    )
                }
                // 查看我的秘钥
                if (!user.value.secret.isNullOrEmpty()) {
                    TextButton(modifier = Modifier.padding(start = 20.dp), onClick = {
                        showDialog.intValue = 11
                    }) { Text(stringResource(R.string.show_secret)) }
                }
                // 配置 KEY
                TextButton(modifier = Modifier.padding(start = 20.dp), onClick = {
                    userViewModel.username.value = user.value.username
                    userViewModel.apiKey.value = user.value.apiKey
                    userViewModel.secretKey.value = user.value.secretKey
                    nav.navigate(Route.PAGE_KEY_SET)
                }) {
                    Text(text = stringResource(R.string.set_key))
                }
                Button(
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = com.hwer.app.ui.theme.Main,
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = com.hwer.app.ui.theme.Main
                    ), onClick = {
                        clear(ctx)
                        nav.navigate(Route.PAGE_WELCOME) {
                            popUpTo(Route.PAGE_MAIN) { inclusive = true }
                        }
                    }, modifier = Modifier
                        .padding(top = 640.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.label_logout))
                }
                Button(
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Gray,
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ), onClick = {
                        showDestroyAccountDialog.value = true
                    }, modifier = Modifier
                        .padding(top = 64.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.label_destroy_account))
                }
                if (showDestroyAccountDialog.value) {
                    AlertDialog(
                        modifier = Modifier.background(Bg),
                        onDismissRequest = {
                            showDestroyAccountDialog.value = false
                        }, title = {
                            Text(stringResource(R.string.warning))
                        }, text = {
                            Text(stringResource(R.string.confirm_destroy_account))
                        }, confirmButton = {
                            TextButton(onClick = {
                                userViewModel.destroyAccount(user.value)
                                showDestroyAccountDialog.value = false
                                clear(ctx)
                                nav.navigate(Route.PAGE_WELCOME) {
                                    popUpTo(Route.PAGE_MAIN) { inclusive = true }
                                }
                            }) { Text(stringResource(R.string.confirm)) }
                        }, dismissButton = {
                            TextButton(onClick = { showDestroyAccountDialog.value = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
            PullToRefreshContainer(modifier = Modifier.align(Alignment.TopCenter), state = state)
            val apiKey = remember { mutableStateOf("") }
            apiKey.value = user.value.apiKey ?: ""
            val secretKey = remember { mutableStateOf("") }
            secretKey.value = user.value.secretKey ?: ""
            if (showDialog.intValue == 11) {
                LaunchedEffect("hideSecret") {
                    userViewModel.hideSecret(user.value)
                }
                Dialog(onDismissRequest = { }, content = {
                    Column(
                        Modifier
                            .background(Bg)
                            .fillMaxWidth()
                            .border(
                                0.dp, Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            color = Color.Yellow,
                            text = stringResource(R.string.show_secret_tip)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            modifier = Modifier.padding(8.dp),
                            color = Color.White,
                            text = user.value.secret.orEmpty()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                if (!user.value.secret.isNullOrEmpty()) {
                                    val cm =
                                        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    ClipData.newPlainText(
                                        ctx.getString(R.string.show_secret),
                                        user.value.secret
                                    )?.let {
                                        cm.setPrimaryClip(it)
                                        user.value.secret = ""
                                        showDialog.intValue = 0
                                    }
                                }
                            }) { Text(stringResource(R.string.copy)) }
                        }
                    }
                })
            }

        }
        if (userViewModel.userUiState == UserUiState.Loading) {
            Comp.Loading(Modifier.fillMaxSize())
        } else if (userViewModel.userUiState is UserUiState.Success && flag.value) {
            flag.value = false
            val result = (userViewModel.userUiState as UserUiState.Success).result
            if (result.code == 0) {
                Toast.makeText(ctx, result.msg, Toast.LENGTH_SHORT).show()
                user.value.allow = 0
                return
            }
            val userRes = result.data
            if (userRes != null) {
                showDialog.intValue = 0
                user.value = userRes
                allowStrategy.value = if (user.value.allow == 1) "开" else "关"
            }
        }
    }
}