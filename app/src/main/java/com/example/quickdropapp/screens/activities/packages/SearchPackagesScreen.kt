package com.example.quickdropapp.screens.activities.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.quickdropapp.composables.packages.PackageCard
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.packages.Package
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
    var isLoading by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noPackages = navBackStackEntry?.arguments?.getBoolean("noPackages") ?: false
    val hasError = navBackStackEntry?.arguments?.getBoolean("error") ?: false

    // Capture the context here within the composable scope
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val parentEntry = navController.getBackStackEntry("startDelivery/$userId")
        val parentPackages = parentEntry.savedStateHandle.get<List<Package>>("packages") ?: emptyList()
        println("SearchPackagesScreen: Loaded packages from savedStateHandle = $parentPackages")
        packages = parentPackages
        isLoading = false
    }

    Scaffold(
        containerColor = SandBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable,
                        modifier = Modifier
                            .size(32.dp)
                            .background(SandBeige.copy(alpha = 0.2f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Beschikbare Pakketten",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pakketten binnen je radius",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                color = GreenSustainable,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(16.dp)
                            )
                        }
                        hasError -> {
                            Text(
                                text = "Er is een fout opgetreden bij het zoeken",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        noPackages || packages.isEmpty() -> {
                            Text(
                                text = "Geen pakketten gevonden in je buurt",
                                color = DarkGreen.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                items(packages) { packageItem ->
                    PackageCard(
                        packageItem = packageItem,
                        onAccept = { packageId ->
                            // Use the captured context here
                            val apiService = RetrofitClient.create(context)
                            val deliveryRequest = com.example.quickdropapp.models.DeliveryRequest(
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
                                        errorMessage = "Fout bij aanmaken levering: ${response.code()}"
                                        println("Error creating delivery: ${response.code()} - ${response.errorBody()?.string()}")
                                    }
                                }

                                override fun onFailure(call: Call<Delivery>, t: Throwable) {
                                    errorMessage = "Netwerkfout: ${t.message}"
                                    println("Network failure: ${t.message}")
                                }
                            })
                        }
                    )
                }

                item {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}