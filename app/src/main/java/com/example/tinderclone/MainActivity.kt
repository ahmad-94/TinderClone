package com.example.tinderclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tinderclone.navigation.Screen
import com.example.tinderclone.navigation.TinderNavHost
import com.example.tinderclone.ui.theme.TinderCloneTheme
import com.example.tinderclone.viewmodel.TCViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinderCloneTheme {
                val navController = rememberNavController()
                val vm = hiltViewModel<TCViewModel>()
                val screens = listOf(
                    Screen.Swipe,
                    Screen.Chat,
                    Screen.Profile
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (vm.signedIn.value && screens.any { it.route == currentDestination?.route }) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                screens.forEach { screen ->
                                    NavigationBarItem(
                                        label = { screen.title?.let { Text(it) } },
                                        icon = {
                                            screen.icon?.let {
                                                Icon(
                                                    imageVector = it,
                                                    contentDescription = screen.title
                                                )
                                            }
                                        },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    TinderNavHost(
                        navController = navController,
                        vm = vm,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
