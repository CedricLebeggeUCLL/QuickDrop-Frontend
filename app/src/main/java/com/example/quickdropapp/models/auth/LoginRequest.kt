package com.example.quickdropapp.models.auth

data class LoginRequest(
    val identifier: String, // Gebruikersnaam of e-mail
    val password: String
)