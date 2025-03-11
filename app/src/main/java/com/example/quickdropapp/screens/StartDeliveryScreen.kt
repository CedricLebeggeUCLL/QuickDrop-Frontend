package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.AddressInputField
import com.example.quickdropapp.models.*
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun StartDeliveryScreen(navController: NavController, userId: Int) {
    var startAddress by remember { mutableStateOf(Address()) }
    var destinationAddress by remember { mutableStateOf(Address()) }
    var pickupRadius by remember { mutableStateOf("30.0") }
    var dropoffRadius by remember { mutableStateOf("40.0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var courierId by remember { mutableStateOf<Int?>(null) }
    var currentCourier by remember { mutableStateOf<Courier?>(null) }

    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        if (userId <= 0) {
            errorMessage = "Ongeldige user ID: $userId"
            return@LaunchedEffect
        }
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                if (response.isSuccessful) {
                    val courier = response.body()
                    courierId = courier?.id
                    currentCourier = courier
                    println("Fetched courierId: $courierId for userId: $userId, data: $courier")
                } else {
                    errorMessage = "Kon koerier niet vinden: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                    println("Failed to fetch courier: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                errorMessage = "Netwerkfout bij ophalen koerier: ${t.message}"
                println("Network failure fetching courier: ${t.message}")
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
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
                            AddressInputField(
                                label = "Startadres",
                                address = startAddress,
                                onAddressChange = { startAddress = it }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AddressInputField(
                                label = "Bestemmingsadres",
                                address = destinationAddress,
                                onAddressChange = { destinationAddress = it }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = pickupRadius,
                                    onValueChange = { pickupRadius = it.filter { char -> char.isDigit() || char == '.' } },
                                    label = { Text("Ophaalradius (km)") },
                                    placeholder = { Text("Bijv. 30.0") },
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
                                    placeholder = { Text("Bijv. 40.0") },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            println("Button clicked - Checking state: courierId=$courierId, inputs: startAddress=$startAddress, destinationAddress=$destinationAddress, pickupRad=$pickupRadius, dropoffRad=$dropoffRadius")
                            if (courierId == null || currentCourier == null) {
                                errorMessage = "Koerier-ID of koerierdata niet geladen, probeer opnieuw"
                                println("Crash point: Courier ID or data is null")
                                return@Button
                            }

                            val pickupRad = pickupRadius.toDoubleOrNull()
                            val dropoffRad = dropoffRadius.toDoubleOrNull()

                            if (pickupRad == null || dropoffRad == null) {
                                errorMessage = "Vul geldige radius in: pickupRad=$pickupRad, dropoffRad=$dropoffRad"
                                println("Crash point: Invalid radius detected")
                                return@Button
                            }

                            if (startAddress.street_name.isBlank() || startAddress.house_number.isBlank() || startAddress.postal_code.isBlank() || startAddress.city.isNullOrBlank() || startAddress.country.isNullOrBlank()) {
                                errorMessage = "Een geldig startadres is verplicht (straat, huisnummer, postcode, stad, land)"
                                return@Button
                            }
                            if (destinationAddress.street_name.isBlank() || destinationAddress.house_number.isBlank() || destinationAddress.postal_code.isBlank() || destinationAddress.city.isNullOrBlank() || destinationAddress.country.isNullOrBlank()) {
                                errorMessage = "Een geldig bestemmingsadres is verplicht (straat, huisnummer, postcode, stad, land)"
                                return@Button
                            }

                            val updateData = CourierUpdateRequest(
                                start_address = startAddress,
                                destination_address = destinationAddress,
                                pickup_radius = pickupRad.toFloat(),
                                dropoff_radius = dropoffRad.toFloat(),
                                availability = true
                            )

                            println("Attempting to update courier $courierId with data: $updateData")

                            apiService.updateCourier(courierId!!, updateData).enqueue(object : Callback<Courier> {
                                override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                    println("API response received: code=${response.code()}, body=${response.body()}")
                                    if (response.isSuccessful) {
                                        successMessage = "Locatie en radius succesvol ingesteld!"
                                        errorMessage = null
                                        currentCourier = response.body()
                                        println("Courier updated successfully: ${response.body()}")

                                        searchPackages(apiService, userId, startAddress, destinationAddress, pickupRad, dropoffRad, navController)
                                    } else {
                                        val errorBody = response.errorBody()?.string() ?: "Geen details"
                                        errorMessage = "Fout bij updaten: ${response.code()} - $errorBody"
                                        successMessage = null
                                        println("Update failed: ${response.code()} - $errorBody")
                                    }
                                }

                                override fun onFailure(call: Call<Courier>, t: Throwable) {
                                    errorMessage = "Netwerkfout: ${t.message}"
                                    successMessage = null
                                    println("Network failure during update: ${t.message}")
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                        shape = RoundedCornerShape(16.dp),
                        enabled = true
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

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

fun searchPackages(
    apiService: ApiService,
    userId: Int,
    startAddress: Address,
    destinationAddress: Address,
    pickupRadius: Double,
    dropoffRadius: Double,
    navController: NavController
) {
    val searchRequest = SearchRequest(
        user_id = userId,
        start_address = startAddress,
        destination_address = destinationAddress,
        pickup_radius = pickupRadius,
        dropoff_radius = dropoffRadius,
        use_current_as_start = false
    )
    println("Sending search request: $searchRequest")
    apiService.searchPackages(searchRequest).enqueue(object : Callback<SearchResponse> {
        override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
            println("Search packages response received: code=${response.code()}, body=${response.body()}")
            if (response.isSuccessful) {
                val result = response.body()
                println("Parsed search response: $result")
                if (result != null) {
                    println("Packages in response: ${result.packages}")
                    if (result.packages.isNotEmpty()) {
                        println("Packages found, navigating to searchPackages/$userId")
                        navController.navigate("searchPackages/$userId") {
                            popUpTo("startDelivery/$userId") { inclusive = false }
                            launchSingleTop = true
                        }
                    } else {
                        println("No packages found for userId: $userId, navigating anyway for debugging")
                        navController.navigate("searchPackages/$userId") {
                            popUpTo("startDelivery/$userId") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                } else {
                    println("Search response is null, navigating for debugging")
                    navController.navigate("searchPackages/$userId") {
                        popUpTo("startDelivery/$userId") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            } else {
                println("Failed to search packages: ${response.code()} - ${response.errorBody()?.string()}, navigating for debugging")
                navController.navigate("searchPackages/$userId") {
                    popUpTo("startDelivery/$userId") { inclusive = false }
                    launchSingleTop = true
                }
            }
        }

        override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
            println("Network failure during package search: ${t.message}, navigating for debugging")
            navController.navigate("searchPackages/$userId") {
                popUpTo("startDelivery/$userId") { inclusive = false }
                launchSingleTop = true
            }
        }
    })
}