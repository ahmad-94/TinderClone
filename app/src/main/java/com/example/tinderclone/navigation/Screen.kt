package com.example.tinderclone.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Swipe : Screen("swipe", "Swipe", Icons.Default.Style)
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}
