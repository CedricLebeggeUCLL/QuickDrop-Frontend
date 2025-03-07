package com.example.quickdropapp.models

data class Courier(
    val id: Int? = null,
    val user_id: Int,
    val current_location: Map<String, Double>? = null, // { lat, lng }
    val destination: Map<String, Double>? = null,     // { lat, lng }
    val pickup_radius: Double = 5.0,                 // in km
    val dropoff_radius: Double = 5.0,                // in km
    val availability: Boolean = true
)