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
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

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
    fun addPackage(@Body packageData: PackageRequest): Call<Package>

    @PUT("packages/{id}")
    fun updatePackage(@Path("id") id: Int, @Body packageData: PackageRequest): Call<Package>

    @DELETE("packages/{id}")
    fun deletePackage(@Path("id") id: Int): Call<Void>

    @GET("packages/{id}/track")
    fun trackPackage(@Path("id") id: Int): Call<Map<String, Any>>

    @POST("packages/search")
    fun searchPackages(@Body searchRequest: SearchRequest): Call<SearchResponse>

    @GET("packages/user/{userId}")
    fun getPackagesByUserId(@Path("userId") userId: Int): Call<List<Package>>

    @GET("packages/stats/{userId}")
    fun getPackageStats(@Path("userId") userId: Int): Call<PackageStats>

    // Couriers Endpoints
    @GET("couriers")
    fun getCouriers(): Call<List<Courier>>

    @GET("couriers/{id}")
    fun getCourierById(@Path("id") id: Int): Call<Courier>

    @GET("couriers/user/{userId}")
    fun getCourierByUserId(@Path("userId") userId: Int): Call<Courier>

    @POST("couriers/become")
    fun becomeCourier(@Body courier: CourierRequest): Call<Courier>

    @PUT("couriers/{id}")
    fun updateCourier(@Path("id") id: Int, @Body courierData: CourierUpdateRequest): Call<Courier>

    @DELETE("couriers/{id}")
    fun deleteCourier(@Path("id") id: Int): Call<Void>

    // Deliveries Endpoints
    @GET("deliveries")
    fun getDeliveries(): Call<List<Delivery>>

    @GET("deliveries/{id}")
    fun getDeliveryById(@Path("id") id: Int): Call<Delivery>

    @POST("deliveries")
    fun createDelivery(@Body delivery: DeliveryRequest): Call<Delivery>

    @PUT("deliveries/{id}")
    fun updateDelivery(@Path("id") id: Int, @Body delivery: DeliveryUpdate): Call<Delivery>

    @DELETE("deliveries/{id}")
    fun cancelDelivery(@Path("id") id: Int): Call<Void>

    @GET("deliveries/history/{userId}")
    fun getDeliveryHistory(@Path("userId") userId: Int): Call<List<Delivery>>

    @GET("deliveries/courier/{userId}")
    fun getCourierDeliveries(@Path("userId") userId: Int): Call<List<Delivery>>

    @GET("deliveries/stats/{userId}")
    fun getDeliveryStats(@Path("userId") userId: Int): Call<DeliveryStats>
}