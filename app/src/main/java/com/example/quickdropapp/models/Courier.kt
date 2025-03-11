package com.example.quickdropapp.models

data class Courier(
    val id: Int? = null,
    val user_id: Int,
    val current_address_id: Int? = null,
    val start_address_id: Int,
    val destination_address_id: Int,
    val pickup_radius: Float? = 5.0f,
    val dropoff_radius: Float? = 5.0f,
    val availability: Boolean? = true,
    val itsme_code: String? = null,
    val license_number: String? = null
)

// Request dataklasse voor het aanmelden als courier
data class CourierRequest(
    val user_id: Int,
    val itsme_code: String,
    val license_number: String? = null
)

// Update dataklasse voor het bijwerken van een courier
data class CourierUpdateRequest(
    val start_address: Address? = null,
    val destination_address: Address? = null,
    val pickup_radius: Float? = null,
    val dropoff_radius: Float? = null,
    val availability: Boolean? = null
)