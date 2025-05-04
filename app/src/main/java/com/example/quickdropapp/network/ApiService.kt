package com.example.quickdropapp.network

import com.example.quickdropapp.models.*
import com.example.quickdropapp.models.auth.LoginRequest
import com.example.quickdropapp.models.auth.LoginResponse
import com.example.quickdropapp.models.auth.RefreshTokenRequest
import com.example.quickdropapp.models.auth.RefreshTokenResponse
import com.example.quickdropapp.models.auth.User
import com.example.quickdropapp.models.courier.Courier
import com.example.quickdropapp.models.courier.CourierRegistrationRequest
import com.example.quickdropapp.models.courier.CourierUpdateRequest
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.packages.PackageRequest
import com.example.quickdropapp.models.packages.SearchRequest
import com.example.quickdropapp.models.packages.SearchResponse
import com.example.quickdropapp.models.tracking.DeliveryTrackingInfo
import com.example.quickdropapp.models.tracking.TrackingInfo
import retrofit2.Call
import retrofit2.http.*

// Data class for location update request body
data class LocationUpdate(
    val lat: Double,
    val lng: Double
)

// Data classes for Routes API request/response
data class ComputeRoutesRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val travelMode: String = "DRIVE",
    val routingPreference: String = "TRAFFIC_AWARE",
    val computeAlternativeRoutes: Boolean = false,
    val routeModifiers: RouteModifiers = RouteModifiers(),
    val languageCode: String = "nl-NL",
    val units: String = "METRIC"
)

data class Waypoint(
    val location: Location
)

data class Location(
    val latLng: LatLng
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class RouteModifiers(
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,
    val avoidFerries: Boolean = false
)

data class ComputeRoutesResponse(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val polyline: Polyline,
    val distanceMeters: Int,
    val duration: String // Bijv. "558s"
)

data class Polyline(
    val encodedPolyline: String
)

// Data class for itsme callback response
data class ItsmeCallbackResponse(
    val message: String
)

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

    @POST("users/refresh")
    fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Call<RefreshTokenResponse>

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: Int, @Body user: User): Call<User>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Void>

    @POST("users/forgot-password")
    fun forgotPassword(@Body request: Map<String, String>): Call<Map<String, String>>

    @POST("users/reset-password/{token}")
    fun resetPassword(@Path("token") token: String, @Body request: Map<String, String>): Call<Map<String, String>>

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
    fun trackPackage(@Path("id") id: Int): Call<TrackingInfo>

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
    fun becomeCourier(@Body courier: CourierRegistrationRequest): Call<Courier>

    @PUT("couriers/{id}")
    fun updateCourier(@Path("id") id: Int, @Body courierData: CourierUpdateRequest): Call<Courier>

    @DELETE("couriers/{id}")
    fun deleteCourier(@Path("id") id: Int): Call<Void>

    @POST("couriers/{id}/location")
    fun updateCourierLocation(
        @Path("id") courierId: Int,
        @Body location: LocationUpdate
    ): Call<Void>

    // itsme Callback Endpoint
    @POST("couriers/itsme-callback")
    fun itsmeCallback(@Body request: Map<String, Any>): Call<ItsmeCallbackResponse>

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

    @GET("deliveries/user/{userId}")
    fun getDeliveriesByUserId(@Path("userId") userId: Int): Call<List<Delivery>>

    @GET("deliveries/stats/{userId}")
    fun getDeliveryStats(@Path("userId") userId: Int): Call<DeliveryStats>

    @GET("deliveries/{id}/track")
    fun trackDelivery(@Path("id") id: Int): Call<DeliveryTrackingInfo>

    // Routes API Endpoint
    @POST("directions/v2:computeRoutes")
    @Headers("X-Goog-FieldMask: routes.polyline.encodedPolyline,routes.distanceMeters,routes.duration")
    fun computeRoutes(
        @Body request: ComputeRoutesRequest,
        @Header("X-Goog-Api-Key") apiKey: String
    ): Call<ComputeRoutesResponse>
}