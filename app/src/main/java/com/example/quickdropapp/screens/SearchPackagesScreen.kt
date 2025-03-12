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
import androidx.navigation.compose.currentBackStackEntryAsState // Toegevoegd
import com.example.quickdropapp.composables.PackageCard
import com.example.quickdropapp.models.*
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import androidx.navigation.NavBackStackEntry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchPackagesScreen(navController: NavController, userId: Int) {
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noPackages = navBackStackEntry?.arguments?.getBoolean("noPackages") ?: false
    val hasError = navBackStackEntry?.arguments?.getBoolean("error") ?: false

    val apiService = RetrofitClient.instance

    LaunchedEffect(Unit) {
        val parentEntry = navController.getBackStackEntry("startDelivery/$userId")
        val parentPackages = parentEntry.savedStateHandle.get<List<Package>>("packages") ?: emptyList()
        println("SearchPackagesScreen: Loaded packages from savedStateHandle = $parentPackages")
        packages = parentPackages
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
        }

        item {
            if (isLoading) {
                CircularProgressIndicator(
                    color = GreenSustainable,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                )
            } else if (hasError) {
                Text(
                    text = "Er is een fout opgetreden bij het zoeken naar pakketten",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (noPackages || packages.isEmpty()) {
                Text(
                    text = "Geen pakketten gevonden binnen je zoekcriteria",
                    color = DarkGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Text(
                text = "Debug: Aantal pakketten = ${packages.size}",
                color = DarkGreen,
                style = MaterialTheme.typography.bodySmall
            )
        }

        items(packages) { packageItem ->
            PackageCard(
                packageItem = packageItem,
                onAccept = { packageId ->
                    val deliveryRequest = DeliveryRequest(
                        user_id = userId,
                        package_id = packageId
                    )

                    apiService.createDelivery(deliveryRequest).enqueue(object : Callback<Delivery> {
                        override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                            if (response.isSuccessful) {
                                println("Delivery created successfully: ${response.body()}")
                                navController.navigate("viewDeliveries/$userId") {
                                    popUpTo("searchPackages/$userId") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "No error details available"
                                errorMessage = "Fout bij aanmaken levering: ${response.code()} - $errorBody"
                                println("Error creating delivery: ${response.code()} - $errorBody")
                                println("Raw response: ${response.raw()}")
                            }
                        }

                        override fun onFailure(call: Call<Delivery>, t: Throwable) {
                            errorMessage = "Netwerkfout bij aanmaken levering: ${t.message}"
                            println("Network failure creating delivery: ${t.message}")
                        }
                    })
                }
            )
        }
    }
}