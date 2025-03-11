package com.example.quickdropapp.models

data class Address(
    val id: Int? = null,
    val street_name: String = "", // Default lege string
    val house_number: String = "", // Default lege string
    val extra_info: String? = null,
    val postal_code: String = "" // Default lege string
)