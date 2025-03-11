package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.PackageCard // Nieuwe import
import com.example.quickdropapp.models.*
import com.example.quickdropapp.network.RetrofitClient
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
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var courierId by remember { mutableStateOf<Int?>(null) }
    var startAddress by remember { mutableStateOf<Address?>(null) }
    var destinationAddress by remember { mutableStateOf<Address?>(null) }

    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        if (userId <= 0) {
            errorMessage = "Ongeldige user ID: $userId"
            isLoading = false
            return@LaunchedEffect
        }

        // Haal de koerier op om de adressen te verkrijgen
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                if (response.isSuccessful) {
                    val courier = response.body()
                    if (courier != null) {
                        courierId = courier.id
                        // Simuleer start- en destination-adressen (in echte app haal je deze op via courier.start_address_id en courier.destination_address_id)
                        startAddress = Address(
                            street_name = "Startstraat",
                            house_number = "1",
                            postal_code = "1000"
                        )
                        destinationAddress = Address(
                            street_name = "Eindstraat",
                            house_number = "2",
                            postal_code = "2000"
                        )

                        val searchRequest = SearchRequest(
                            user_id = userId,
                            start_address = startAddress ?: Address(),
                            destination_address = destinationAddress ?: Address(),
                            pickup_radius = (courier.pickup_radius ?: 5.0f).toDouble(),
                            dropoff_radius = (courier.dropoff_radius ?: 5.0f).toDouble(),
                            use_current_as_start = false
                        )

                        println("Searching packages with request: $searchRequest")

                        apiService.searchPackages(searchRequest).enqueue(object : Callback<SearchResponse> {
                            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                                if (response.isSuccessful) {
                                    packages = response.body()?.packages?.filterNotNull() ?: emptyList()
                                    errorMessage = null
                                } else {
                                    println("Search failed: ${response.code()} - ${response.errorBody()?.string()}")
                                }
                                isLoading = false
                            }

                            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                                errorMessage = "Netwerkfout bij zoeken: ${t.message}"
                                println("Network failure: ${t.message}")
                                isLoading = false
                            }
                        })
                    } else {
                        errorMessage = "Geen koeriergegevens gevonden voor user ID: $userId"
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
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

                successMessage?.let {
                    Text(
                        text = it,
                        color = GreenSustainable,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    errorMessage != null -> {
                        // Error message is already shown above
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
                                PackageCard(
                                    packageItem = packageItem,
                                    onAccept = { packageId ->
                                        if (courierId == null) {
                                            errorMessage = "Koerier-ID niet geladen, probeer opnieuw"
                                            return@PackageCard
                                        }

                                        // Maak een nieuwe DeliveryRequest
                                        val deliveryRequest = DeliveryRequest(
                                            user_id = userId,
                                            package_id = packageId,
                                            start_address = startAddress ?: Address(), // Gebruik simulatie voor nu
                                            destination_address = destinationAddress ?: Address(), // Gebruik simulatie voor nu
                                            pickup_radius = null,
                                            dropoff_radius = null
                                        )

                                        apiService.createDelivery(deliveryRequest).enqueue(object : Callback<Delivery> {
                                            override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                                                if (response.isSuccessful) {
                                                    // Update de pakketstatus naar "assigned"
                                                    val updatedPackage = PackageRequest(
                                                        user_id = userId,
                                                        description = packageItem.description,
                                                        pickup_address = startAddress ?: Address(),
                                                        dropoff_address = destinationAddress ?: Address(),
                                                        status = "assigned"
                                                    )
                                                    apiService.updatePackage(packageId, updatedPackage).enqueue(object : Callback<Package> {
                                                        override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                                            if (response.isSuccessful) {
                                                                successMessage = "Pakket succesvol geaccepteerd!"
                                                                errorMessage = null
                                                                // Navigeer naar "Mijn Leveringen"
                                                                navController.navigate("viewDeliveries/$userId") {
                                                                    popUpTo("searchPackages/$userId") { inclusive = true }
                                                                }
                                                            } else {
                                                                successMessage = null
                                                            }
                                                        }

                                                        override fun onFailure(call: Call<Package>, t: Throwable) {
                                                            errorMessage = "Netwerkfout bij updaten pakket: ${t.message}"
                                                            successMessage = null
                                                        }
                                                    })
                                                } else {
                                                    successMessage = null
                                                }
                                            }

                                            override fun onFailure(call: Call<Delivery>, t: Throwable) {
                                                errorMessage = "Netwerkfout bij aanmaken levering: ${t.message}"
                                                successMessage = null
                                            }
                                        })
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}