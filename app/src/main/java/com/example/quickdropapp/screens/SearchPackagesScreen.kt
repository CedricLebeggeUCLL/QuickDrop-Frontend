package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchPackagesScreen(navController: NavController, userId: Int) {
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<com.example.quickdropapp.models.Courier> {
            override fun onResponse(call: Call<com.example.quickdropapp.models.Courier>, response: Response<com.example.quickdropapp.models.Courier>) {
                if (response.isSuccessful) {
                    val courier = response.body()
                    if (courier != null) {
                        val searchRequest = ApiService.SearchRequest(
                            user_id = userId,
                            start_location = courier.current_location ?: mapOf("lat" to 0.0, "lng" to 0.0),
                            destination = courier.destination ?: mapOf("lat" to 0.0, "lng" to 0.0),
                            pickup_radius = courier.pickup_radius,
                            dropoff_radius = courier.dropoff_radius
                        )

                        println("Searching packages with request: $searchRequest")

                        apiService.searchPackages(searchRequest).enqueue(object : Callback<ApiService.SearchResponse> {
                            override fun onResponse(call: Call<ApiService.SearchResponse>, response: Response<ApiService.SearchResponse>) {
                                if (response.isSuccessful) {
                                    packages = response.body()?.packages ?: emptyList()
                                    errorMessage = null
                                } else {
                                    errorMessage = "Fout bij zoeken: ${response.code()} - ${response.errorBody()?.string()}"
                                }
                                isLoading = false
                            }

                            override fun onFailure(call: Call<ApiService.SearchResponse>, t: Throwable) {
                                errorMessage = "Netwerkfout bij zoeken: ${t.message}"
                                isLoading = false
                            }
                        })
                    } else {
                        errorMessage = "Geen koeriergegevens gevonden"
                        isLoading = false
                    }
                } else {
                    errorMessage = "Kon koerier niet ophalen: ${response.code()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<com.example.quickdropapp.models.Courier>, t: Throwable) {
                errorMessage = "Netwerkfout bij ophalen koerier: ${t.message}"
                isLoading = false
            }
        })
    }

    Scaffold(
        containerColor = SandBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .shadow(4.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable
                    )
                }
                Text(
                    text = "Zoek Pakketten",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Beschikbare pakketten",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    packages.isEmpty() -> {
                        Text(
                            text = "Geen pakketten gevonden binnen je zoekcriteria",
                            color = DarkGreen,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(packages) { packageItem ->
                                PackageCard(packageItem)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackageCard(packageItem: Package) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SandBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pakket ID: ${packageItem.id}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Beschrijving: ${packageItem.description ?: "Geen beschrijving"}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ophaallocatie: ${packageItem.pickupLocation.let { if (it.size >= 2) "(${it[0]}, ${it[1]})" else "Onbekend" }}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Afleverlocatie: ${packageItem.dropoffLocation.let { if (it.size >= 2) "(${it[0]}, ${it[1]})" else "Onbekend" }}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
        }
    }
}