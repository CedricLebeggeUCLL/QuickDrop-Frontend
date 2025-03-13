package com.example.quickdropapp.models

data class TrackingInfo(
    val packageId: Int,
    val status: String,
    val currentLocation: LatLng,
    val pickupAddress: Address,  // Jouw bestaande Address-klasse
    val dropoffAddress: Address, // Jouw bestaande Address-klasse
    val estimatedDelivery: String
)

data class LatLng(
    val lat: Double,
    val lng: Double
)