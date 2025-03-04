package com.example.quickdropapp.network

import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.models.Delivery
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/deliveries")
    fun getDeliveries(): Call<List<Delivery>>

    @GET("api/deliveries/{id}")
    fun getDeliveryById(@Path("id") id: Int): Call<Delivery>

    @POST("api/deliveries")
    fun createDelivery(@Body delivery: Delivery): Call<Delivery>

    @PUT("api/deliveries/{id}")
    fun updateDelivery(@Path("id") id: Int, @Body delivery: Delivery): Call<Delivery>

    @DELETE("api/deliveries/{id}")
    fun deleteDelivery(@Path("id") id: Int): Call<Void>

    // Optioneel: Voeg endpoints voor /api/couriers toe als je die wilt testen
    @GET("api/couriers")
    fun getCouriers(): Call<List<Courier>>

    @GET("api/couriers/{id}")
    fun getCourierById(@Path("id") id: Int): Call<Courier>

    @POST("api/couriers")
    fun createCourier(@Body courier: Courier): Call<Courier>

    @PUT("api/couriers/{id}")
    fun updateCourier(@Path("id") id: Int, @Body courier: Courier): Call<Courier>

    @DELETE("api/couriers/{id}")
    fun deleteCourier(@Path("id") id: Int): Call<Void>
}