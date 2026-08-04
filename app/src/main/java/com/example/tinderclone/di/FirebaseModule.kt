package com.example.tinderclone.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideMongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://shiravandahmad941_db_user:YEtny4AHRxYPSO0H@ac-awsov4e-shard-00-00.kfyemnx.mongodb.net:27017,ac-awsov4e-shard-00-01.kfyemnx.mongodb.net:27017,ac-awsov4e-shard-00-02.kfyemnx.mongodb.net:27017/?ssl=true&replicaSet=atlas-pbtmlo-shard-0&authSource=admin&appName=TinderClone")
        val settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(settings)
    }
}
