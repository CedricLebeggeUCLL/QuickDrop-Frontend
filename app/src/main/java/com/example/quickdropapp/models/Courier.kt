package com.example.quickdropapp.models

import com.google.gson.annotations.SerializedName

data class Courier(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("start_address_id") val startAddressId: Int? = null,
    @SerializedName("destination_address_id") val destinationAddressId: Int? = null,
    @SerializedName("pickup_radius") val pickupRadius: Float? = 5.0f,
    @SerializedName("dropoff_radius") val dropoffRadius: Float? = 5.0f,
    @SerializedName("availability") val availability: Boolean? = true,
    @SerializedName("current_lat") val currentLat: Double? = null,
    @SerializedName("current_lng") val currentLng: Double? = null
)

data class CourierDetails(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("birth_date") val birthDate: String, // Format: "dd/mm/yyyy"
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("national_number") val nationalNumber: String,
    @SerializedName("nationality") val nationality: String,
    @SerializedName("itsme_verified") val itsmeVerified: Boolean = false
)

data class CourierRegistrationRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("birth_date") val birthDate: String, // Format: "dd/mm/yyyy"
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("national_number") val nationalNumber: String,
    @SerializedName("nationality") val nationality: String
)

// Toegevoegd: Data class voor het bijwerken van een koerier (PUT /couriers/{id})
data class CourierUpdateRequest(
    @SerializedName("start_address") val startAddress: Address? = null,
    @SerializedName("destination_address") val destinationAddress: Address? = null,
    @SerializedName("pickup_radius") val pickupRadius: Float? = null,
    @SerializedName("dropoff_radius") val dropoffRadius: Float? = null,
    @SerializedName("availability") val availability: Boolean? = null
)