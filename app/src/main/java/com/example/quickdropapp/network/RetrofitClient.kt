package com.example.quickdropapp.network

import android.content.Context
import com.example.quickdropapp.data.AuthDataStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api/" // For emulator, adjust to your IP for physical device

    fun create(context: Context): ApiService {
        // Logging interceptor for debugging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Authentication interceptor to add JWT token
        val authInterceptor = Interceptor { chain ->
            val token = AuthDataStore.getToken(context)
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        // Build OkHttpClient with interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        // Build Retrofit instance with custom client
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}