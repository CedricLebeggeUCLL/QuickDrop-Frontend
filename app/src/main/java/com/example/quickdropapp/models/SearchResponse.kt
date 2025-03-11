package com.example.quickdropapp.models

data class SearchResponse(
    val message: String,
    val packages: List<Package>? // Maak nullable
)