package com.example.quickdropapp.models

data class Courier(
    val id: Int? = null,
    val user_id: Int,
    val current_location: Location? = null,
    val destination: Location? = null,
    val availability: Boolean
)

data class Location(
    val lat: Double,
    val lng: Double
)