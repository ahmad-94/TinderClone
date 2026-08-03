package com.example.tinderclone.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.delay
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

@Composable
fun SignupScreen(navController: NavController, vm: TCViewModel) {
    val messageBarState = rememberMessageBarState()

    LaunchedEffect(Unit) {
        vm.inProgress.value = true
        delay(1500)
        vm.inProgress.value = false
    }

    LaunchedEffect(Unit) {
        vm.errorFlow.collect { ex ->
            messageBarState.addError(ex)
        }
    }

    if (vm.signedIn.value) {
        navController.navigate(Screen.Swipe.route) {
            popUpTo(Screen.Signup.route) { inclusive = true }
        }
    }

    ContentWithMessageBar(messageBarState = messageBarState) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (vm.inProgress.value) {
                CommonProgressSpinner()
            }

            if (!vm.inProgress.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        text = "Signup",
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
                            vm.onSignup(emailState.value, passState.value)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "SIGN UP")
                    }

                    Text(
                        text = "Already have an account? Go to login",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                navController.navigate(Screen.Signin.route)
                            }
                    )
                }
            }
        }
    }
}
