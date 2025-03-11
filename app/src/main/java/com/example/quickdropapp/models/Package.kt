package com.example.quickdropapp.models

data class Package(
    val id: Int? = null,
    val user_id: Int,
    val description: String? = null,
    val pickup_address_id: Int,
    val dropoff_address_id: Int,
    val status: String? = "pending",
    val created_at: String? = null
)

// Request dataklasse voor het aanmaken/bijwerken van een package
data class PackageRequest(
    val user_id: Int,
    val description: String? = null,
    val pickup_address: Address,
    val dropoff_address: Address,
    val status: String? = "pending"
)