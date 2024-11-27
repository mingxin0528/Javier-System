package com.hwer.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hwer.app.config.Route
import com.hwer.app.data.getUsername
import com.hwer.app.ui.page.KeysPage.KeySet
import com.hwer.app.ui.page.LoginPage
import com.hwer.app.ui.page.MainPage
import com.hwer.app.ui.page.RegisterPage
import com.hwer.app.ui.theme.Bg
import com.hwer.app.ui.theme.Default
import com.hwer.app.ui.theme.HwerTheme
import com.hwer.app.ui.theme.Main
import com.hwer.app.ui.theme.MainDisabled
import com.hwer.app.vm.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HwerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Nav(this)
                }
            }
        }
    }
}

@Composable
fun Nav(ctx: Context) {
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (!getUsername(ctx).isNullOrEmpty()) Route.PAGE_MAIN else Route.PAGE_WELCOME
    ) {
        composable(Route.PAGE_WELCOME) { Welcome(ctx, navController, userViewModel) }
        composable(Route.PAGE_LOGIN) { LoginPage.Login(ctx, navController, userViewModel) }
        composable(Route.PAGE_REGISTER) { RegisterPage.Register(ctx, navController, userViewModel) }
        composable(Route.PAGE_MAIN) { MainPage.Main(ctx, navController, userViewModel) }
        composable(Route.PAGE_PROFILE) { Profile(ctx, navController, userViewModel) }
        composable(Route.PAGE_KEY_SET) { KeySet(ctx, navController, userViewModel) }
    }
}

@Composable
fun Welcome(
    ctx: Context,
    nav: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(color = Bg)
            .padding(32.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                textAlign = TextAlign.Center,
                color = Main,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    nav.navigate(Route.PAGE_LOGIN)
                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Main,
                    disabledContentColor = MainDisabled,
                    disabledContainerColor = MainDisabled
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(shape = RoundedCornerShape(32.dp), border = BorderStroke(2.dp, Main))
            ) {
                Text(stringResource(R.string.login), fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {
                    nav.navigate(Route.PAGE_REGISTER)
                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Default,
                    disabledContentColor = MainDisabled,
                    disabledContainerColor = MainDisabled
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(shape = RoundedCornerShape(32.dp), border = BorderStroke(2.dp, Default))
            ) {
                Text(stringResource(R.string.register), fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun Profile(
    ctx: Context,
    nav: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(color = Bg)
            .padding(32.dp)
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HwerTheme {
        val navController = rememberNavController()
        val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
        NavHost(navController = navController, startDestination = Route.PAGE_WELCOME) {
            composable(Route.PAGE_WELCOME) {
                Welcome(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
            composable(Route.PAGE_LOGIN) {
                LoginPage.Login(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
            composable(Route.PAGE_REGISTER) {
                RegisterPage.Register(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
            composable(Route.PAGE_MAIN) {
                MainPage.Main(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
            composable(Route.PAGE_PROFILE) {
                Profile(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
            composable(Route.PAGE_KEY_SET) {
                Profile(
                    LocalContext.current,
                    navController,
                    userViewModel
                )
            }
        }
        MainPage.Main(LocalContext.current, navController, userViewModel)
    }
}