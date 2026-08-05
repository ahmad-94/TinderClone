package com.example.tinderclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tinderclone.ui.auth.SigninScreen
import com.example.tinderclone.ui.auth.SignupScreen
import com.example.tinderclone.ui.chat.ChatScreen
import com.example.tinderclone.ui.profile.ProfileScreen
import com.example.tinderclone.ui.swipe.SwipeScreen
import com.example.tinderclone.viewmodel.TCViewModel

@Composable
fun TinderNavHost(
    navController: NavHostController,
    vm: TCViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (vm.signedIn.value) Screen.Swipe.route else Screen.Signup.route,
        modifier = modifier
    ) {
        composable(Screen.Signup.route) {
            SignupScreen(navController, vm)
        }
        composable(Screen.Signin.route) {
            SigninScreen(navController, vm)
        }
        composable(Screen.Swipe.route) {
            SwipeScreen(vm)
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, vm)
        }
    }
}
