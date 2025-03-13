package com.example.quickdropapp.screens.activities.deliveries

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry // Toegevoegd
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
    var packages = remember { mutableStateOf<List<Package>>(emptyList()) }

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
                    println("Fetched courierId: $courierId for userId: $userId")
                } else {
                    errorMessage = "Kon koerier niet vinden: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                    println("Failed to fetch courier: ${response.code()} - ${response.errorBody()?.string()}")
                    courierId = userId // Gebruik userId als fallback
                }
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                errorMessage = "Netwerkfout bij ophalen koerier: ${t.message}"
                println("Network failure fetching courier: ${t.message}")
                courierId = userId // Fallback naar userId
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
                            if (courierId == null) {
                                errorMessage = "Koerier-ID niet geladen, probeer opnieuw"
                                return@Button
                            }

                            val pickupRad = pickupRadius.toDoubleOrNull()
                            val dropoffRad = dropoffRadius.toDoubleOrNull()

                            if (pickupRad == null || dropoffRad == null) {
                                errorMessage = "Vul geldige radius in"
                                return@Button
                            }

                            if (startAddress.street_name.isBlank() || startAddress.house_number.isBlank() ||
                                startAddress.postal_code.isBlank() || startAddress.city.isNullOrBlank() ||
                                startAddress.country.isNullOrBlank() ||
                                destinationAddress.street_name.isBlank() || destinationAddress.house_number.isBlank() ||
                                destinationAddress.postal_code.isBlank() || destinationAddress.city.isNullOrBlank() ||
                                destinationAddress.country.isNullOrBlank()) {
                                errorMessage = "Alle adresvelden zijn verplicht"
                                return@Button
                            }

                            val updateData = CourierUpdateRequest(
                                start_address = startAddress,
                                destination_address = destinationAddress,
                                pickup_radius = pickupRad.toFloat(),
                                dropoff_radius = dropoffRad.toFloat(),
                                availability = true
                            )

                            val currentEntry = navController.currentBackStackEntry
                            apiService.updateCourier(courierId!!, updateData).enqueue(object : Callback<Courier> {
                                override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                    if (response.isSuccessful) {
                                        successMessage = "Locatie en radius succesvol ingesteld!"
                                        errorMessage = null
                                        searchPackages(apiService, userId, startAddress, destinationAddress, pickupRad, dropoffRad, packages, navController, currentEntry)
                                    } else {
                                        errorMessage = "Fout bij updaten: ${response.code()} - ${response.errorBody()?.string()}"
                                        println("Update failed: ${response.code()} - ${response.errorBody()?.string()}")
                                        if (response.code() == 404) {
                                            // Hercontroleer koerier
                                            apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
                                                override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                                    if (response.isSuccessful) {
                                                        courierId = response.body()?.id
                                                        apiService.updateCourier(courierId!!, updateData).enqueue(object : Callback<Courier> {
                                                            override fun onResponse(call: Call<Courier>, innerResponse: Response<Courier>) {
                                                                if (innerResponse.isSuccessful) {
                                                                    successMessage = "Locatie en radius succesvol ingesteld na hercontrole!"
                                                                    errorMessage = null
                                                                    searchPackages(apiService, userId, startAddress, destinationAddress, pickupRad, dropoffRad, packages, navController, currentEntry)
                                                                } else {
                                                                    errorMessage = "Fout bij herhaalde update: ${innerResponse.code()} - ${innerResponse.errorBody()?.string()}"
                                                                }
                                                            }
                                                            override fun onFailure(call: Call<Courier>, t: Throwable) {
                                                                errorMessage = "Netwerkfout bij herhaalde update: ${t.message}"
                                                            }
                                                        })
                                                    } else {
                                                        errorMessage = "Kon koerier niet herstellen: ${response.code()} - ${response.errorBody()?.string()}"
                                                    }
                                                }
                                                override fun onFailure(call: Call<Courier>, t: Throwable) {
                                                    errorMessage = "Netwerkfout bij hercontrole: ${t.message}"
                                                }
                                            })
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<Courier>, t: Throwable) {
                                    errorMessage = "Netwerkfout: ${t.message}"
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                        shape = RoundedCornerShape(16.dp),
                        enabled = courierId != null && startAddress.street_name.isNotBlank() && startAddress.house_number.isNotBlank() &&
                                startAddress.postal_code.isNotBlank() && startAddress.city?.isNotBlank() == true &&
                                startAddress.country?.isNotBlank() == true &&
                                destinationAddress.street_name.isNotBlank() && destinationAddress.house_number.isNotBlank() &&
                                destinationAddress.postal_code.isNotBlank() && destinationAddress.city?.isNotBlank() == true &&
                                destinationAddress.country?.isNotBlank() == true &&
                                pickupRadius.isNotBlank() && pickupRadius.toDoubleOrNull() != null &&
                                dropoffRadius.isNotBlank() && dropoffRadius.toDoubleOrNull() != null
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
    packages: MutableState<List<Package>>,
    navController: NavController,
    currentEntry: NavBackStackEntry? // Nu correct geïmporteerd
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
            println("Raw response: ${response.raw()}")
            if (response.isSuccessful) {
                val result = response.body()
                println("Parsed result: $result")
                if (result != null) {
                    val packageList = result.packages?.filterNotNull() ?: emptyList()
                    println("Package list after parsing: $packageList")
                    packages.value = packageList
                    // Save to savedStateHandle
                    currentEntry?.savedStateHandle?.set("packages", packageList)
                    if (packageList.isNotEmpty()) {
                        navController.navigate("searchPackages/$userId") {
                            popUpTo("startDelivery/$userId") { inclusive = false }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("searchPackages/$userId?noPackages=true") {
                            popUpTo("startDelivery/$userId") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                } else {
                    navController.navigate("searchPackages/$userId?noPackages=true") {
                        popUpTo("startDelivery/$userId") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            } else {
                println("Error response: ${response.errorBody()?.string()}")
                navController.navigate("searchPackages/$userId?error=true") {
                    popUpTo("startDelivery/$userId") { inclusive = false }
                    launchSingleTop = true
                }
            }
        }

        override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
            println("Network failure: ${t.message}")
            navController.navigate("searchPackages/$userId?error=true") {
                popUpTo("startDelivery/$userId") { inclusive = false }
                launchSingleTop = true
            }
        }
    })
}