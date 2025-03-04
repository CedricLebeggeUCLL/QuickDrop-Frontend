package com.example.quickdropapp.models

data class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val password: String, // In productie: hash dit!
    val role: String = "user"
)