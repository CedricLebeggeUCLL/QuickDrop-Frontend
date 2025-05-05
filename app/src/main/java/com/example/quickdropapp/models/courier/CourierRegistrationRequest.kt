package com.example.quickdropapp.models.courier

import com.google.gson.annotations.SerializedName

data class CourierRegistrationRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("birth_date") val birthDate: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val address: String,
    val city: String,
    @SerializedName("postal_code") val postalCode: String,
    val country: String,
    @SerializedName("national_number") val nationalNumber: String,
    val nationality: String
)