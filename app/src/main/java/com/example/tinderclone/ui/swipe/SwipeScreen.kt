package com.example.tinderclone.ui.swipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tinderclone.ui.components.CommonProgressSpinner
import com.example.tinderclone.ui.components.TinderCard
import com.example.tinderclone.viewmodel.TCViewModel
import kotlinx.coroutines.delay

@Composable
fun SwipeScreen(vm: TCViewModel) {
    var profiles by remember { mutableStateOf(vm.cardsData.value) }

    LaunchedEffect(Unit) {
        if (!vm.isFirstTime.value) {
            vm.inProgress.value = true
            delay(1500)
            vm.inProgress.value = false
        }
        vm.isFirstTime.value = false
        vm.getCardsData()
    }

    LaunchedEffect(vm.cardsData.value) {
        profiles = vm.cardsData.value
    }

    if (vm.inProgress.value) {
        CommonProgressSpinner()
    }

    if (!vm.inProgress.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (profiles.isEmpty()) {
                    Text(
                        text = "No more profiles!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                } else {
                    profiles.reversed().forEach { profile ->
                        TinderCard(
                            profile = profile,
                            onSwipeLeft = {
                                profiles = profiles.filter { it.id != profile.id }
                            },
                            onSwipeRight = {
                                profiles = profiles.filter { it.id != profile.id }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        if (profiles.isNotEmpty()) {
                            profiles = profiles.filter { it.id != profiles.last().id }
                        }
                    },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dislike",
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { 
                        if (profiles.isNotEmpty()) {
                            profiles = profiles.filter { it.id != profiles.last().id }
                        }
                    },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Green
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
