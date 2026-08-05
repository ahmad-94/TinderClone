package com.example.tinderclone.di

import android.content.Context
import com.cloudinary.android.MediaManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideCloudinary(@ApplicationContext context: Context): MediaManager {
        val config = mapOf(
            "cloud_name" to "eucpvbeo",
            "api_key" to "217924152998582",
            "api_secret" to "uWDYYCU6diSrUy_ufkkXJCZEyX0"
        )
        try {
            MediaManager.init(context, config)
        } catch (_: Exception) {
            // Already initialized
        }
        return MediaManager.get()
    }
}
