package com.example.tinderclone.model

data class TinderProfile(
    val id: String,
    val name: String,
    val age: Int,
    val bio: String,
    val imageUrls: List<Int> // Using Int for resource IDs for now
)
