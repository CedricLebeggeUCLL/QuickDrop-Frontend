package com.example.quickdropapp.network

import com.example.quickdropapp.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Users Endpoints
    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @POST("users/register")
    fun registerUser(@Body user: User): Call<User>

    @POST("users/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<User>

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: Int, @Body user: User): Call<User>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Void>

    // Packages Endpoints
    @GET("packages")
    fun getPackages(): Call<List<Package>>

    @GET("packages/{id}")
    fun getPackageById(@Path("id") id: Int): Call<Package>

    @POST("packages")
    fun addPackage(@Body packageData: Package): Call<Package>

    @PUT("packages/{id}")
    fun updatePackage(@Path("id") id: Int, @Body packageData: Package): Call<Package>

    @DELETE("packages/{id}")
    fun deletePackage(@Path("id") id: Int): Call<Void>

    @GET("packages/{id}/track")
    fun trackPackage(@Path("id") id: Int): Call<Map<String, Any>>

    // Couriers Endpoints
    @GET("couriers")
    fun getCouriers(): Call<List<Courier>>

    @GET("couriers/{id}")
    fun getCourierById(@Path("id") id: Int): Call<Courier>

    @GET("couriers/user/{userId}")
    fun getCourierByUserId(@Path("userId") userId: Int): Call<Courier>

    @POST("couriers")
    fun becomeCourier(@Body courier: Courier): Call<Courier>

    @PUT("couriers/{id}")
    fun updateCourier(@Path("id") id: Int, @Body courierData: Map<String, Any>): Call<Courier>

    @DELETE("couriers/{id}")
    fun deleteCourier(@Path("id") id: Int): Call<Void>

    // Deliveries Endpoints
    @GET("deliveries")
    fun getDeliveries(): Call<List<Delivery>>

    @GET("deliveries/{id}")
    fun getDeliveryById(@Path("id") id: Int): Call<Delivery>

    @POST("deliveries")
    fun createDelivery(@Body delivery: Delivery): Call<Delivery>

    @PUT("deliveries/{id}")
    fun updateDelivery(@Path("id") id: Int, @Body delivery: Delivery): Call<Delivery>

    @DELETE("deliveries/{id}")
    fun cancelDelivery(@Path("id") id: Int): Call<Void>

    @GET("deliveries/users/{userId}")
    fun getDeliveryHistory(@Path("userId") id: Int): Call<List<Delivery>>

    @POST("packages/search")
    fun searchPackages(@Body searchRequest: SearchRequest): Call<SearchResponse>

    // Search Request and Response
    data class SearchRequest(
        val user_id: Int,
        val start_location: Map<String, Double>,
        val destination: Map<String, Double>,
        val pickup_radius: Double,
        val dropoff_radius: Double
    )

    data class SearchResponse(
        val message: String,
        val packages: List<Package>
    )
}