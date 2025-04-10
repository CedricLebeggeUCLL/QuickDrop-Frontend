package com.example.quickdropapp.models.auth

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)