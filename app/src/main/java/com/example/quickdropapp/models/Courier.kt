package com.example.quickdropapp.models

import com.google.gson.annotations.SerializedName

data class Courier(
    val id: Int? = null,
    val user_id: Int,
    @SerializedName("current_location")
    val current_location: List<Double>? = null, // Gewijzigd naar List<Double>
    val destination: List<Double>? = null,      // Gewijzigd naar List<Double>
    @SerializedName("pickup_radius")
    val pickup_radius: Double = 5.0,
    @SerializedName("dropoff_radius")
    val dropoff_radius: Double = 5.0,
    val availability: Boolean = true,
    @SerializedName("itsme_code")
    val itsme_code: String? = null,
    @SerializedName("license_number")
    val license_number: String? = null
)