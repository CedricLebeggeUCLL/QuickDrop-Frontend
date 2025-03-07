package com.example.quickdropapp.models

import com.google.gson.annotations.SerializedName

data class Delivery(
    val id: Int? = null,
    val package_id: Int,
    val courier_id: Int,
    @SerializedName("pickup_location")
    val pickupLocation: List<Double>, // [lat, lng]
    @SerializedName("dropoff_location")
    val dropoffLocation: List<Double>, // [lat, lng]
    @SerializedName("pickup_time")
    val pickupTime: String? = null, // ISO 8601 formaat
    @SerializedName("delivery_time")
    val deliveryTime: String? = null, // ISO 8601 formaat
    val status: String = "assigned" // ENUM: 'assigned', 'picked_up', 'in_transit', 'delivered'
)