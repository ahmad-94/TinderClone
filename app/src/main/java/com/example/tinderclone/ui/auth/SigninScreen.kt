package com.example.tinderclone.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tinderclone.R
import com.example.tinderclone.navigation.Screen
import com.example.tinderclone.ui.components.CommonProgressSpinner
import com.example.tinderclone.viewmodel.TCViewModel
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import kotlinx.coroutines.delay

@Composable
fun SigninScreen(navController: NavController, vm: TCViewModel) {
    val messageBarState = rememberMessageBarState()

//    LaunchedEffect(Unit) {
//        vm.inProgress.value = true
//        delay(1500)
//        vm.inProgress.value = false
//    }

    LaunchedEffect(Unit) {
        vm.errorFlow.collect { ex ->
            messageBarState.addError(ex)
        }
    }

    LaunchedEffect(vm.signedIn.value) {
        if (vm.signedIn.value) {
            navController.navigate(Screen.Swipe.route) {
                popUpTo(Screen.Signin.route) { inclusive = true }
            }
        }
    }

    ContentWithMessageBar(messageBarState = messageBarState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (vm.inProgress.value) {
                CommonProgressSpinner()
            }

            if (!vm.inProgress.value) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val emailState = remember { mutableStateOf("") }
                    val passState = remember { mutableStateOf("") }

                    Image(
                        painter = painterResource(id = R.drawable.fire),
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(top = 16.dp)
                            .padding(8.dp)
                    )

                    Text(
                        text = "Login",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 30.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        modifier = Modifier.padding(8.dp),
                        label = { Text(text = "Email") }
                    )

                    OutlinedTextField(
                        value = passState.value,
                        onValueChange = { passState.value = it },
                        modifier = Modifier.padding(8.dp),
                        label = { Text(text = "Password") }
                    )

                    Button(
                        onClick = {
                            vm.onSignin(emailState.value, passState.value)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "LOGIN")
                    }

                    Text(
                        text = "New user? Go to signup",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                navController.navigate(Screen.Signup.route) {
                                    popUpTo(Screen.Signin.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                    )
                }
            }
        }
    }
}
