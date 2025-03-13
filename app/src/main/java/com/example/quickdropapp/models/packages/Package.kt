package com.example.quickdropapp.models.packages

import com.example.quickdropapp.models.Address
import com.google.gson.annotations.SerializedName

data class Package(
    val id: Int,
    val user_id: Int,
    val description: String? = null,
    val pickup_address_id: Int,
    val dropoff_address_id: Int,
    val status: String? = "pending",
    val created_at: String? = null,
    @SerializedName("pickupAddress") val pickupAddress: Address? = null, // Toegevoegd
    @SerializedName("dropoffAddress") val dropoffAddress: Address? = null // Toegevoegd
)

// Request dataklasse voor het aanmaken/bijwerken van een package
data class PackageRequest(
    val user_id: Int,
    val description: String? = null,
    val pickup_address: Address,
    val dropoff_address: Address,
    val status: String? = "pending"
)