package com.example.tinderclone.viewmodel

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.mongodb.client.MongoClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.Binary
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val mongoClient: MongoClient
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var signedIn = mutableStateOf(false)
    var isFirstTime = mutableStateOf(false)

    private val _errorFlow = MutableSharedFlow<Exception>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
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
                        val user = hashMapOf(
                            "email" to email,
                            "uid" to uid
                        )
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                signedIn.value = true
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
                } else {
                    onError(task.exception)
                }
                inProgress.value = false
            }
    }

    fun uploadImageToMongo(imageBytes: ByteArray, onSuccess: (String) -> Unit) {
        inProgress.value = true
        val uid = auth.currentUser?.uid ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val database = mongoClient.getDatabase("tinderclone")
                val collection = database.getCollection("images")
                
                val imageId = UUID.randomUUID().toString()
                val doc = Document("_id", imageId)
                    .append("userId", uid)
                    .append("data", Binary(imageBytes))
                    .append("createdAt", System.currentTimeMillis())
                
                collection.insertOne(doc)
                
                withContext(Dispatchers.Main) {
                    inProgress.value = false
                    onSuccess(imageId)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
    }

    private fun onError(exception: Exception?) {
        android.util.Log.e("TCViewModel", "Authentication Error", exception)
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
