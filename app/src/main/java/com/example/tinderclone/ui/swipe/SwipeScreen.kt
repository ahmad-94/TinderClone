package com.example.tinderclone.ui.swipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tinderclone.ui.components.CommonProgressSpinner
import com.example.tinderclone.model.SwipeDirection
import com.example.tinderclone.ui.components.TinderCard
import com.example.tinderclone.viewmodel.TCViewModel

@Composable
fun SwipeScreen(vm: TCViewModel) {
    val profiles by vm.cardsData
    val swipeTrigger by vm.swipeTrigger
    val matchProfile by vm.matchNotification

//    LaunchedEffect(Unit) {
//        if (!vm.isFirstTime.value) {
//            vm.inProgress.value = true
//            delay(1500)
//            vm.inProgress.value = false
//        }
//        vm.isFirstTime.value = true
//        vm.getCardsData()
//    }

    LaunchedEffect(Unit) {
        vm.getCardsData()
    }

    // Match Dialog
    matchProfile?.let { profile ->
        AlertDialog(
            onDismissRequest = { vm.clearMatchNotification() },
            title = { Text(text = "It's a Match!", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = profile.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(150.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = "You and ${profile.name} liked each other.", modifier = Modifier.padding(top = 16.dp))
                }
            },
            confirmButton = {
                Button(onClick = { vm.clearMatchNotification() }) {
                    Text("Awesome!")
                }
            }
        )
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
                                profile.id?.let { vm.onDislike(it) }
                                vm.swipeTrigger.value = null
                            },
                            onSwipeRight = {
                                vm.onLike(profile)
                                vm.swipeTrigger.value = null
                            },
                            swipeTrigger = if (profile == profiles.firstOrNull()) swipeTrigger else null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (profiles.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (profiles.isNotEmpty() && swipeTrigger == null) {
                                vm.swipeTrigger.value = SwipeDirection.Left
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
                            if (profiles.isNotEmpty() && swipeTrigger == null) {
                                vm.swipeTrigger.value = SwipeDirection.Right
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
}
