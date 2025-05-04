package com.example.quickdropapp.models.courier

import kotlinx.serialization.Serializable

@Serializable
data class CourierRegistrationRequest(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val nationalNumber: String,
    val nationality: String
)