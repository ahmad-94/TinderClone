package com.example.tinderclone.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var signedIn = mutableStateOf(false)
    var isFirstTime = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
    }

    fun onSignup(email: String, pass: String) {
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
                                inProgress.value = false
                            }
                    } else {
                        inProgress.value = false
                    }
                } else {
                    inProgress.value = false
                }
            }
    }

    fun onSignin(email: String, pass: String) {
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    isFirstTime.value = true
                }
                inProgress.value = false
            }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
    }
}
