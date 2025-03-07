package com.example.quickdropapp.models

import com.google.gson.annotations.SerializedName

data class Package(
    val id: Int? = null,
    val user_id: Int,
    val description: String? = null,
    @SerializedName("pickup_location")
    val pickupLocation: List<Double>, // [lat, lng]
    @SerializedName("dropoff_location")
    val dropoffLocation: List<Double>, // [lat, lng]
    @SerializedName("pickup_address")
    val pickupAddress: String? = null,
    @SerializedName("dropoff_address")
    val dropoffAddress: String? = null,
    val status: String = "pending", // ENUM: 'pending', 'assigned', 'in_transit', 'delivered'
    @SerializedName("created_at")
    val createdAt: String? = null // ISO 8601 formaat
)