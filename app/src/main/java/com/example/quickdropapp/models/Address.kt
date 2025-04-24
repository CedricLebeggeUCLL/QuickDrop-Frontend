package com.example.quickdropapp.models

data class Address(
    val id: Int? = null,
    val street_name: String = "",
    val house_number: String = "",
    val postal_code: String = "",
    val extra_info: String? = null,
    val city: String? = null,
    val country: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)