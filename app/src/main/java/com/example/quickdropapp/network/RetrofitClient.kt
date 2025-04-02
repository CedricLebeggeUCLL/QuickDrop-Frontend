package com.example.quickdropapp.network

import android.content.Context
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.models.auth.RefreshTokenRequest
import com.example.quickdropapp.models.auth.RefreshTokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api/" // Adjust for your environment

    fun create(context: Context): ApiService {
        // Logging interceptor for debugging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            // Add the current access token to the request
            val accessToken = AuthDataStore.getAccessToken(context)
            val requestBuilder = originalRequest.newBuilder()
            if (accessToken != null) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            }

            val response = chain.proceed(requestBuilder.build())

            // Handle 401 Unauthorized response
            if (response.code == 401) {
                response.close()

                synchronized(this) {
                    val refreshToken = AuthDataStore.getRefreshToken(context)
                    if (refreshToken != null) {
                        // Refresh the token synchronously using runBlocking
                        val refreshResponse = runBlocking {
                            refreshToken(context, refreshToken) // Assume this is a suspend function
                        }
                        if (refreshResponse != null) {
                            // Update the access token synchronously
                            runBlocking {
                                AuthDataStore.updateAccessToken(context, refreshResponse.accessToken)
                            }
                            // Retry the original request with the new token
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                                .build()
                            return@Interceptor chain.proceed(newRequest)
                        }
                    }

                    // If refresh fails, clear auth data in the background
                    CoroutineScope(Dispatchers.IO).launch {
                        AuthDataStore.clearAuthData(context)
                    }
                }
            }

            response
        }

        // Build OkHttpClient with interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        // Build Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    // Suspend function to refresh the token
    private suspend fun refreshToken(context: Context, refreshToken: String): RefreshTokenResponse? {
        val apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = apiService.refreshToken(RefreshTokenRequest(refreshToken))
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}