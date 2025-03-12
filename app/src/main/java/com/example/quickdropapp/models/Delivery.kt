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

data class DeliveryRequest(
    val user_id: Int,
    val package_id: Int
)

// Update dataklasse voor het bijwerken van een delivery
data class DeliveryUpdate(
    val status: String,
    val pickup_time: String? = null,
    val delivery_time: String? = null
)