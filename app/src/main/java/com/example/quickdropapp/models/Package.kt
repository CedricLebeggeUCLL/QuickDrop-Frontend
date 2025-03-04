package com.example.quickdropapp.models

data class Package(
    val id: Int? = null,
    val user_id: Int,
    val description: String,
    val status: String = "pending",
    val created_at: String? = null // ISO 8601 formaat, bijv. "2025-03-02T14:00:00.000Z"
)