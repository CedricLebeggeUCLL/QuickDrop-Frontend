package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun StartDeliveryScreen(navController: NavController, userId: Int) {
    var currentLatitude by remember { mutableStateOf("") }
    var currentLongitude by remember { mutableStateOf("") }
    var destinationLatitude by remember { mutableStateOf("") }
    var destinationLongitude by remember { mutableStateOf("") }
    var pickupRadius by remember { mutableStateOf("5.0") }
    var dropoffRadius by remember { mutableStateOf("5.0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var courierId by remember { mutableStateOf<Int?>(null) } // Om de koerier-ID op te slaan

    val apiService = RetrofitClient.instance

    // Haal de koerier-ID op bij het laden van het scherm
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                if (response.isSuccessful) {
                    courierId = response.body()?.id
                    println("Courier ID for user $userId: $courierId")
                } else {
                    errorMessage = "Kon koerier niet vinden: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                errorMessage = "Netwerkfout bij ophalen koerier: ${t.message}"
            }
        })
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
                    text = "Start een Levering",
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
                    text = "Stel je beschikbaarheid en zoekcriteria in",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SandBeige),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Huidige locatie
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = currentLatitude,
                                onValueChange = { currentLatitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Huidige Latitude") },
                                placeholder = { Text("Bijv. 50.8503") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = currentLongitude,
                                onValueChange = { currentLongitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Huidige Longitude") },
                                placeholder = { Text("Bijv. 4.3517") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bestemmingslocatie
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = destinationLatitude,
                                onValueChange = { destinationLatitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Bestemming Latitude") },
                                placeholder = { Text("Bijv. 51.2178") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = destinationLongitude,
                                onValueChange = { destinationLongitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Bestemming Longitude") },
                                placeholder = { Text("Bijv. 4.4203") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Radius
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = pickupRadius,
                                onValueChange = { pickupRadius = it.filter { char -> char.isDigit() || char == '.' } },
                                label = { Text("Ophaalradius (km)") },
                                placeholder = { Text("Bijv. 5.0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = dropoffRadius,
                                onValueChange = { dropoffRadius = it.filter { char -> char.isDigit() || char == '.' } },
                                label = { Text("Afleverradius (km)") },
                                placeholder = { Text("Bijv. 5.0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (courierId == null) {
                            errorMessage = "Koerier-ID niet geladen, probeer opnieuw"
                            return@Button
                        }

                        val currentLat = currentLatitude.toDoubleOrNull()
                        val currentLng = currentLongitude.toDoubleOrNull()
                        val destLat = destinationLatitude.toDoubleOrNull()
                        val destLng = destinationLongitude.toDoubleOrNull()
                        val pickupRad = pickupRadius.toDoubleOrNull()
                        val dropoffRad = dropoffRadius.toDoubleOrNull()

                        if (currentLat == null || currentLng == null || destLat == null || destLng == null || pickupRad == null || dropoffRad == null) {
                            errorMessage = "Vul geldige coördinaten en radius in"
                            return@Button
                        }

                        val updateData = mapOf(
                            "current_location" to mapOf("lat" to currentLat, "lng" to currentLng),
                            "destination" to mapOf("lat" to destLat, "lng" to destLng),
                            "pickup_radius" to pickupRad,
                            "dropoff_radius" to dropoffRad,
                            "availability" to true
                        )

                        println("Updating courier $courierId with data: $updateData")

                        apiService.updateCourier(courierId!!, updateData).enqueue(object : Callback<Courier> {
                            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                if (response.isSuccessful) {
                                    successMessage = "Locatie en radius ingesteld!"
                                    errorMessage = null
                                    // Trigger refresh in HomeScreen
                                    navController.previousBackStackEntry?.savedStateHandle?.set("refresh", "true")
                                    // Navigeer naar zoekscherm
                                    navController.navigate("searchPackages/$userId")
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Geen details"
                                    errorMessage = "Fout bij updaten: ${response.code()} - $errorBody"
                                    successMessage = null
                                    println("Error response: ${response.code()} - $errorBody")
                                }
                            }

                            override fun onFailure(call: Call<Courier>, t: Throwable) {
                                errorMessage = "Netwerkfout: ${t.message}"
                                successMessage = null
                                println("Network failure: ${t.message}")
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                    shape = RoundedCornerShape(16.dp),
                    enabled = currentLatitude.isNotBlank() && currentLongitude.isNotBlank() &&
                            destinationLatitude.isNotBlank() && destinationLongitude.isNotBlank() &&
                            pickupRadius.isNotBlank() && dropoffRadius.isNotBlank() && courierId != null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Zoek Pakketten",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Zoek Pakketten",
                            color = SandBeige,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}