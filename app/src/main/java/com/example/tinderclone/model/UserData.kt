package com.example.tinderclone.model

data class UserData(
    val uid: String? = "",
    val name: String? = "",
    val username: String? = "",
    val email: String? = "",
    val bio: String? = "",
    val imageUrl: String? = ""
) {
    fun toMap() = mapOf(
        "uid" to uid,
        "name" to name,
        "username" to username,
        "email" to email,
        "bio" to bio,
        "imageUrl" to imageUrl
    )
}
