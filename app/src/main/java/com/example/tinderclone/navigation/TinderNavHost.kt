package com.example.tinderclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tinderclone.ui.chat.ChatScreen
import com.example.tinderclone.ui.profile.ProfileScreen
import com.example.tinderclone.ui.swipe.SwipeScreen

@Composable
fun TinderNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Swipe.route,
        modifier = modifier
    ) {
        composable(Screen.Swipe.route) {
            SwipeScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
