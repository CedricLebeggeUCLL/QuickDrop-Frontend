package com.example.quickdropapp.models.packages

import com.example.quickdropapp.models.Address

data class SearchRequest(
    val user_id: Int,
    val start_address: Address,
    val destination_address: Address,
    val pickup_radius: Double,
    val dropoff_radius: Double,
    val use_current_as_start: Boolean = false
)