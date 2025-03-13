package com.example.quickdropapp.screens.activities.tracking

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.TrackingInfo
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TrackPackagesScreen(navController: NavController, userId: Int) {
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var selectedPackageId by remember { mutableStateOf<Int?>(null) }
    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val apiService = RetrofitClient.instance

    // Haal de lijst van pakketten op bij het laden van de pagina
    LaunchedEffect(userId) {
        apiService.getPackagesByUserId(userId).enqueue(object : Callback<List<Package>> {
            override fun onResponse(call: Call<List<Package>>, response: Response<List<Package>>) {
                if (response.isSuccessful) {
                    packages = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Fout bij ophalen pakketten: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Package>>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    // Haal trackinginformatie op wanneer een pakket is geselecteerd
    LaunchedEffect(selectedPackageId) {
        selectedPackageId?.let { packageId ->
            apiService.trackPackage(packageId).enqueue(object : Callback<TrackingInfo> {
                override fun onResponse(call: Call<TrackingInfo>, response: Response<TrackingInfo>) {
                    if (response.isSuccessful) {
                        trackingInfo = response.body()
                        errorMessage = null
                    } else {
                        errorMessage = "Fout bij ophalen tracking: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<TrackingInfo>, t: Throwable) {
                    errorMessage = "Netwerkfout: ${t.message}"
                }
            })
        }
    }

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Custom Top Bar
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
                    text = "Track Pakketten",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Laad-, fout- of succesweergave
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage!!, color = Color.Red, fontSize = 16.sp)
                    }
                }
                else -> {
                    // Kaart bovenaan
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            trackingInfo?.currentLocation?.let {
                                com.google.android.gms.maps.model.LatLng(it.lat, it.lng)
                            } ?: com.google.android.gms.maps.model.LatLng(52.3676, 4.9041), // Standaard Amsterdam
                            14f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(isMyLocationEnabled = false)
                            ) {
                                trackingInfo?.let { info ->
                                    Marker(
                                        state = MarkerState(
                                            position = com.google.android.gms.maps.model.LatLng(
                                                info.currentLocation.lat,
                                                info.currentLocation.lng
                                            )
                                        ),
                                        title = "Pakket Locatie",
                                        snippet = "Huidige positie van pakket #${info.packageId}"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lijst van pakketten
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(packages) { pkg ->
                                val pickupCity = pkg.pickupAddress?.city ?: "Onbekend"
                                val dropoffCity = pkg.dropoffAddress?.city ?: "Onbekend"
                                Button(
                                    onClick = { selectedPackageId = pkg.id },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable)
                                ) {
                                    Text(
                                        text = "Track Pakket #${pkg.id}: ${pkg.description ?: "Geen beschrijving"} - Van $pickupCity naar $dropoffCity",
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Statusinformatie onderaan
                        trackingInfo?.let { info ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = SandBeige),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Pakket ID: ${info.packageId}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GreenSustainable
                                    )
                                    Text(
                                        text = "Status: ${info.status}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GreenSustainable
                                    )
                                    Text(
                                        text = "Geschatte aankomst: ${info.estimatedDelivery}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "Afhaaladres: ${info.pickupAddress.street_name} ${info.pickupAddress.house_number}${info.pickupAddress.extra_info?.let { ", $it" } ?: ""}, ${info.pickupAddress.postal_code} ${info.pickupAddress.city ?: ""}${info.pickupAddress.country?.let { ", $it" } ?: ""}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "Afleveradres: ${info.dropoffAddress.street_name} ${info.dropoffAddress.house_number}${info.dropoffAddress.extra_info?.let { ", $it" } ?: ""}, ${info.dropoffAddress.postal_code} ${info.dropoffAddress.city ?: ""}${info.dropoffAddress.country?.let { ", $it" } ?: ""}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}