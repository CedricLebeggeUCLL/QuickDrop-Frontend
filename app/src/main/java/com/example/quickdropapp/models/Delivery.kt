package com.example.quickdropapp.models

data class Delivery(
    val id: Int? = null,
    val package_id: Int,
    val courier_id: Int,
    val pickup_address_id: Int,
    val dropoff_address_id: Int,
    val pickup_time: String? = null,
    val delivery_time: String? = null,
    val status: String? = "assigned"
)

// Request dataklasse voor het aanmaken van een delivery
data class DeliveryRequest(
    val user_id: Int,
    val package_id: Int,
    val start_address: Address,
    val destination_address: Address,
    val pickup_radius: Float? = null,
    val dropoff_radius: Float? = null
)

// Update dataklasse voor het bijwerken van een delivery
data class DeliveryUpdate(
    val status: String,
    val pickup_time: String? = null,
    val delivery_time: String? = null
)