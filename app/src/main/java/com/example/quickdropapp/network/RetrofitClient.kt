package com.example.quickdropapp.network

import android.content.Context
import android.util.Log
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.models.auth.RefreshTokenRequest
import com.example.quickdropapp.models.auth.RefreshTokenResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.4.75:3000/api/"
    private const val GOOGLE_ROUTES_BASE_URL = "https://routes.googleapis.com/"

    // Retrofit-instantie voor beveiligde API-aanroepen (met authInterceptor)
    fun create(context: Context): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val urlPath = originalRequest.url.encodedPath

            // Log de URL om te controleren welke aanroep wordt uitgevoerd
            Log.d("RetrofitClient", "Processing request: $urlPath")

            // Sla token refresh-logica over voor de inlog-API-aanroep
            if (urlPath.endsWith("/auth/login")) {
                Log.d("RetrofitClient", "Skipping token refresh for login request: $urlPath")
                val response = chain.proceed(originalRequest)
                Log.d("RetrofitClient", "Login request response code: ${response.code}")
                return@Interceptor response
            }

            // Voeg het access token toe aan de request (indien beschikbaar)
            val accessToken = AuthDataStore.getAccessToken(context)
            val requestBuilder = originalRequest.newBuilder()
            if (accessToken != null) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            }

            val response = chain.proceed(requestBuilder.build())
            Log.d("RetrofitClient", "Response code: ${response.code}")

            // Pas token refresh-logica alleen toe op andere endpoints
            if (response.code == 401) {
                response.close()
                Log.d("RetrofitClient", "Token expired or invalid, attempting refresh")

                synchronized(this) {
                    val refreshToken = AuthDataStore.getRefreshToken(context)
                    if (refreshToken != null) {
                        Log.d("RetrofitClient", "Using refreshToken: $refreshToken")
                        val refreshResponse = runBlocking {
                            refreshToken(context, refreshToken)
                        }
                        if (refreshResponse != null) {
                            Log.d("RetrofitClient", "Token refreshed: accessToken=${refreshResponse.accessToken}, refreshToken=${refreshResponse.refreshToken}")
                            runBlocking {
                                AuthDataStore.updateAccessToken(context, refreshResponse.accessToken)
                                AuthDataStore.updateRefreshToken(context, refreshResponse.refreshToken)
                            }
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                                .build()
                            return@Interceptor chain.proceed(newRequest)
                        } else {
                            Log.d("RetrofitClient", "Refresh token failed")
                            runBlocking {
                                AuthDataStore.clearAuthData(context)
                            }
                            throw TokenRefreshException("Failed to refresh token, user logged out")
                        }
                    } else {
                        Log.d("RetrofitClient", "No refresh token available")
                        runBlocking {
                            AuthDataStore.clearAuthData(context)
                        }
                        throw TokenRefreshException("No refresh token available, user logged out")
                    }
                }
            }

            response
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    // Nieuwe methode voor een Retrofit-instantie zonder authInterceptor (voor inloggen)
    fun createPublic(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    // Nieuwe methode voor Google Routes API
    fun createRoutesApi(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(GOOGLE_ROUTES_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private suspend fun refreshToken(context: Context, refreshToken: String): RefreshTokenResponse? {
        val apiService = createPublic() // Gebruik de publieke Retrofit-instantie zonder authInterceptor

        val call = apiService.refreshToken(RefreshTokenRequest(refreshToken))
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("RetrofitClient", "Refresh token response: $body")
                body
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("RetrofitClient", "Refresh token failed with body: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("RetrofitClient", "Refresh token exception: ${e.message}")
            null
        }
    }
}

class TokenRefreshException(message: String) : Exception(message)