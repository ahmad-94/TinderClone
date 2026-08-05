package com.example.tinderclone.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.tinderclone.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val cloudinary: MediaManager
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var signedIn = mutableStateOf(false)
    var isFirstTime = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    private val _errorFlow = MutableSharedFlow<Exception>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        if (signedIn.value) {
            getUserData(currentUser?.uid)
        }
    }

    fun onSignup(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            onError(Exception("Please fill in all fields"))
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError(Exception("Please enter a valid email address"))
            return
        }
        if (pass.length < 6) {
            onError(Exception("Password should be at least 6 characters"))
            return
        }

        inProgress.value = true
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val user = UserData(uid = uid, email = email)
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                signedIn.value = true
                                userData.value = user
                                isFirstTime.value = true 
                                inProgress.value = false
                            }
                            .addOnFailureListener {
                                onError(it)
                            }
                    } else {
                        inProgress.value = false
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    fun onSignin(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            onError(Exception("Please fill in all fields"))
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError(Exception("Please enter a valid email address"))
            return
        }

        inProgress.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    isFirstTime.value = true
                    getUserData(auth.currentUser?.uid)
                } else {
                    onError(task.exception)
                }
                inProgress.value = false
            }
    }

    private fun getUserData(uid: String?) {
        if (uid == null) return
        inProgress.value = true
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                userData.value = document.toObject(UserData::class.java)
                inProgress.value = false
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun updateUserData(name: String, username: String, bio: String) {
        val uid = auth.currentUser?.uid ?: return
        inProgress.value = true
        val updateMap = mapOf(
            "name" to name,
            "username" to username,
            "bio" to bio
        )
        db.collection("users").document(uid).update(updateMap)
            .addOnSuccessListener {
                getUserData(uid)
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun uploadImage(imageBytes: ByteArray) {
        inProgress.value = true
        val uid = auth.currentUser?.uid ?: return
        
        cloudinary.upload(imageBytes)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"] as String
                    db.collection("users").document(uid).update("imageUrl", imageUrl)
                        .addOnSuccessListener {
                            getUserData(uid)
                        }
                    inProgress.value = false
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    this@TCViewModel.onError(Exception(error.description))
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
    }

    private fun onError(exception: Exception?) {
        Log.e("TCViewModel", "Error", exception)
        val message = when (exception) {
            is FirebaseAuthWeakPasswordException -> "The password is too weak."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password format."
            is FirebaseAuthUserCollisionException -> "This email is already in use."
            is FirebaseAuthInvalidUserException -> "No such user found or account disabled."
            is FirebaseAuthException -> exception.localizedMessage ?: "Authentication error."
            else -> exception?.localizedMessage ?: "An unexpected error occurred."
        }
        viewModelScope.launch {
            _errorFlow.emit(Exception(message))
        }
        inProgress.value = false
    }
}
