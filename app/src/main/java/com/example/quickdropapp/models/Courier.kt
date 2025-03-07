package com.example.quickdropapp.models

data class Courier(
    val id: Int? = null,
    val user_id: Int,
    val current_location: Map<String, Double>? = null,
    val destination: Map<String, Double>? = null,
    val pickup_radius: Double = 5.0,
    val dropoff_radius: Double = 5.0,
    val availability: Boolean = true,
    val itsme_code: String? = null, // Nieuw veld
    val license_number: String? = null // Nieuw veld
)