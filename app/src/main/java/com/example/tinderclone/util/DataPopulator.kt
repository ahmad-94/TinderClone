package com.example.tinderclone.util

import com.example.tinderclone.data.remote.UserData
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
                imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1787180924/photo-1621784563330-caee0b138a00_qx7jl3.jpg"
            ),
            UserData(
                uid = "dummy2",
                name = "Mark",
                username = "marky_mark",
                email = "mark@example.com",
                bio = "Guitar player and pizza lover.",
                imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1787181047/photo-1539571696357-5a69c17a67c6_sw6bas.jpg"
            ),
            UserData(
                uid = "dummy3",
                name = "Sarah",
                username = "sarah_sun",
                email = "sarah@example.com",
                bio = "Coffee addict and book worm.",
                imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1787180787/photo-1611451444023-7fe9d86fe1d0_bxqmr0.jpg"
            ),
            UserData(
            uid = "dummy4",
            name = "Mary",
            username = "mary",
            email = "mary@example.com",
            bio = "Coffee addict and book worm.",
            imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1787180690/photo-1544005313-94ddf0286df2_zp6y9m.jpg"
        ),
            UserData(
                uid = "dummy5",
                name = "Stephan",
                username = "stephan",
                email = "stephan@example.com",
                bio = "Coffee addict and book worm.",
                imageUrl = "https://res.cloudinary.com/eucpvbeo/image/upload/v1787181220/photo-1688641877066-f8f4ef86bd08_x0t3vb.jpg"
            )
        )

        dummyUsers.forEach { user ->
            db.collection("users").document(user.uid!!).set(user)
        }
    }
}
