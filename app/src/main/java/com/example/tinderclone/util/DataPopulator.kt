package com.example.tinderclone.util

import com.example.tinderclone.model.UserData
import com.google.firebase.firestore.FirebaseFirestore

object DataPopulator {
    fun populateUsers(db: FirebaseFirestore) {
        val dummyUsers = listOf(
            UserData(
                uid = "dummy1",
                name = "Jessica",
                username = "jess_cool",
                email = "jess@example.com",
                bio = "Love hiking and outdoor adventures!",
                imageUrl = "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg"
            ),
            UserData(
                uid = "dummy2",
                name = "Mark",
                username = "marky_mark",
                email = "mark@example.com",
                bio = "Guitar player and pizza lover.",
                imageUrl = "https://res.cloudinary.com/demo/image/upload/w_400,h_400,c_crop,g_face,r_max/face_left.png"
            ),
            UserData(
                uid = "dummy3",
                name = "Sarah",
                username = "sarah_sun",
                email = "sarah@example.com",
                bio = "Coffee addict and book worm.",
                imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1786305040/65_bf5ls8.jpg"
            )
        )

        dummyUsers.forEach { user ->
            db.collection("users").document(user.uid!!).set(user)
        }
    }
}
