package com.example.quickdropapp.models

data class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user" // ENUM: 'user', 'courier', 'admin'
)