package com.example.tinderclone.ui.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tinderclone.navigation.Screen
import com.example.tinderclone.ui.components.CommonProgressSpinner
import com.example.tinderclone.viewmodel.TCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, vm: TCViewModel) {
    val userData by vm.userData // Use property delegation for better reactivity
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    // Sync UI fields with userData changes
    LaunchedEffect(userData) {
        userData?.let {
            name = it.name ?: ""
            username = it.username ?: ""
            bio = it.bio ?: ""
            Log.d("ProfileScreen", "UserData updated in UI: ${it.imageUrl}")
        }
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pickedUri ->
            Log.d("ProfileScreen", "Image picked: $pickedUri")
            val bytes = context.contentResolver.openInputStream(pickedUri)?.readBytes()
            bytes?.let { b ->
                vm.uploadImage(b)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                actions = {
                    TextButton(onClick = {
                        vm.onLogout()
                        navController.navigate(Screen.Signup.route) {
                            popUpTo(0)
                        }
                    }) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val currentImageUrl = userData?.imageUrl
                    if (!currentImageUrl.isNullOrEmpty()) {
                        Log.d("ProfileScreen", "Displaying image: $currentImageUrl")
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onLoading = { Log.d("ProfileScreen", "Loading image...") },
                            onSuccess = { Log.d("ProfileScreen", "Image loaded successfully") },
                            onError = { Log.e("ProfileScreen", "Image load failed: ${it.result.throwable}") }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { vm.updateUserData(name, username, bio) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Save Changes")
                }
            }

            if (vm.inProgress.value) {
                CommonProgressSpinner()
            }
        }
    }
}
