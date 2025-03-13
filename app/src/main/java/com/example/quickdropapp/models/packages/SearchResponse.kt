package com.example.quickdropapp.models.packages

data class SearchResponse(
    val message: String,
    val packages: List<Package>? // Maak nullable
)