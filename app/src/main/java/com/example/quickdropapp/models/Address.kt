// com.example.quickdropapp.models/Address.kt
package com.example.quickdropapp.models

data class Address(
    val street_name: String = "",
    val house_number: String = "",
    val postal_code: String = "",
    val extra_info: String? = null,
    val city: String? = null, // Toegevoegd voor postal_codes tabel
    val country: String? = null // Toegevoegd voor postal_codes tabel
)