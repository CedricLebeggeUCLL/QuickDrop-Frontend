package com.example.quickdropapp.models.tracking

import com.example.quickdropapp.models.Address

data class DeliveryTrackingInfo(
    val deliveryId: Int,
    val status: String,
    val currentLocation: Location,
    val pickupAddress: Address,
    val dropoffAddress: Address,
    val estimatedDelivery: String
) {
    data class Location(
        val lat: Double,
        val lng: Double
    )
}