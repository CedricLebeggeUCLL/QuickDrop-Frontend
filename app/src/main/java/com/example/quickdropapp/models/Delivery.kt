package com.example.quickdropapp.models

import com.google.gson.annotations.SerializedName

data class Delivery(
    val id: Int? = null,
    @SerializedName("package_id")
    val package_id: Int? = null,
    @SerializedName("courier_id")
    val courier_id: Int? = null,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("pickup_location")
    val pickupLocation: List<Double>? = null,
    @SerializedName("dropoff_location")
    val dropoffLocation: List<Double>? = null,
    @SerializedName("pickup_time")
    val pickupTime: String? = null,
    @SerializedName("delivery_time")
    val deliveryTime: String? = null,
    val status: String? = null
)

data class DeliveryUpdate(
    val id: Int? = null,
    val status: String? = null,
    @SerializedName("pickup_time")
    val pickupTime: String? = null,
    @SerializedName("delivery_time")
    val deliveryTime: String? = null
)