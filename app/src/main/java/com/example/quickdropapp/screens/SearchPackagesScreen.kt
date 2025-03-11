package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.network.ApiService
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

    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        if (userId <= 0) {
            errorMessage = "Ongeldige user ID: $userId"
            isLoading = false
            return@LaunchedEffect
        }

        apiService.getCourierByUserId(userId).enqueue(object : Callback<com.example.quickdropapp.models.Courier> {
            override fun onResponse(call: Call<com.example.quickdropapp.models.Courier>, response: Response<com.example.quickdropapp.models.Courier>) {
                if (response.isSuccessful) {
                    val courier = response.body()
                    if (courier != null) {
                        courierId = courier.id
                        val searchRequest = ApiService.SearchRequest(
                            user_id = userId,
                            start_location = courier.current_location ?: listOf(0.0, 0.0),
                            destination = courier.destination ?: listOf(0.0, 0.0),
                            pickup_radius = courier.pickup_radius ?: 5.0,
                            dropoff_radius = courier.dropoff_radius ?: 5.0
                        )

                        println("Searching packages with request: $searchRequest")

                        apiService.searchPackages(searchRequest).enqueue(object : Callback<ApiService.SearchResponse> {
                            override fun onResponse(call: Call<ApiService.SearchResponse>, response: Response<ApiService.SearchResponse>) {
                                if (response.isSuccessful) {
                                    packages = response.body()?.packages?.filterNotNull() ?: emptyList()
                                    errorMessage = null
                                } else {
                                    errorMessage = "Fout bij zoeken: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                                    println("Search failed: ${response.code()} - ${response.errorBody()?.string()}")
                                }
                                isLoading = false
                            }

                            override fun onFailure(call: Call<ApiService.SearchResponse>, t: Throwable) {
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
                    errorMessage = "Kon koerier niet ophalen: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
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

                                        // Maak een nieuwe Delivery
                                        val delivery = Delivery(
                                            id = null, // Wordt door backend ingevuld
                                            package_id = packageId,
                                            courier_id = courierId!!, // Non-null assertion na de check
                                            user_id = userId, // Voeg user_id toe
                                            pickupLocation = packageItem.pickupLocation,
                                            dropoffLocation = packageItem.dropoffLocation,
                                            pickupTime = null,
                                            deliveryTime = null,
                                            status = "assigned"
                                        )

                                        apiService.createDelivery(delivery).enqueue(object : Callback<Delivery> {
                                            override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                                                if (response.isSuccessful) {
                                                    // Update de pakketstatus naar "assigned"
                                                    val updatedPackage = packageItem.copy(status = "assigned")
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
                                                                errorMessage = "Fout bij updaten pakket: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                                                                successMessage = null
                                                            }
                                                        }

                                                        override fun onFailure(call: Call<Package>, t: Throwable) {
                                                            errorMessage = "Netwerkfout bij updaten pakket: ${t.message}"
                                                            successMessage = null
                                                        }
                                                    })
                                                } else {
                                                    errorMessage = "Fout bij aanmaken levering: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
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

@Composable
fun PackageCard(packageItem: Package, onAccept: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SandBeige,
            contentColor = DarkGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pakket ID: ${packageItem.id ?: "Onbekend"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Text(
                    text = packageItem.status?.uppercase() ?: "PENDING",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (packageItem.status == "pending") GreenSustainable else DarkGreen.copy(alpha = 0.6f),
                    modifier = Modifier
                        .background(
                            color = if (packageItem.status == "pending") GreenSustainable.copy(alpha = 0.1f) else SandBeige,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Beschrijving: ${packageItem.description ?: "Geen beschrijving"}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ophaallocatie: ${packageItem.pickupLocation.let { loc ->
                    if (loc.size >= 2) "(${loc[0]}, ${loc[1]})" else "Onbekend"
                }}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Afleverlocatie: ${packageItem.dropoffLocation.let { loc ->
                    if (loc.size >= 2) "(${loc[0]}, ${loc[1]})" else "Onbekend"
                }}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (packageItem.status == "pending") {
                Button(
                    onClick = {
                        packageItem.id?.let { onAccept(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenSustainable,
                        contentColor = SandBeige
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Accepteren",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}