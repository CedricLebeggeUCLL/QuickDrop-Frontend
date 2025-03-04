package com.example.quickdropapp.models

data class Delivery(
    val id: Int? = null,
    val package_id: Int,
    val courier_id: Int,
    val pickup_time: String? = null, // ISO 8601 formaat, bijv. "2025-02-28T14:00:00.000Z"
    val delivery_time: String? = null // ISO 8601 formaat
)