package com.example.quickdropapp.models.auth

data class LoginResponse(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)