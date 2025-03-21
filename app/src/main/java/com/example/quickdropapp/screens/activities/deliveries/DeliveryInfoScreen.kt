package com.example.quickdropapp.screens.activities.deliveries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.deliveries.DeliveryInfoCard
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.DeliveryUpdate
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DeliveryInfoScreen(navController: NavController, deliveryId: Int) {
    var delivery by remember { mutableStateOf<Delivery?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val apiService = RetrofitClient.instance

    LaunchedEffect(deliveryId) {
        apiService.getDeliveryById(deliveryId).enqueue(object : Callback<Delivery> {
            override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                if (response.isSuccessful) {
                    delivery = response.body()
                    isLoading = false
                } else {
                    println("Fout bij het laden van levering: ${response.code()} - ${response.message()}")
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<Delivery>, t: Throwable) {
                println("Netwerkfout bij het laden van levering: ${t.message}")
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f)) // Match SendPackageScreen
                    )
                )
        ) {
            // Clean header (matched with SendPackageScreen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige) // Solid background, no gradient
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
                    text = "Levering Details",
                    color = GreenSustainable, // Match SendPackageScreen
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Match SendPackageScreen
            }

            // Content with animated card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    delivery == null -> {
                        Text(
                            text = "Levering niet gevonden",
                            color = DarkGreen.copy(alpha = 0.8f), // Match SendPackageScreen
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    else -> {
                        delivery?.let { del ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 500))
                            ) {
                                DeliveryInfoCard(
                                    delivery = del,
                                    onUpdateStatus = ::updateDeliveryStatus,
                                    onCancel = ::cancelDelivery,
                                    apiService = apiService,
                                    navController = navController,
                                    onDeliveryUpdated = { newDelivery ->
                                        delivery = newDelivery
                                        if (newDelivery.status == "picked_up" || newDelivery.status == "delivered") {
                                            navController.popBackStack()
                                        }
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

// Helper-functie om de status bij te werken
fun updateDeliveryStatus(
    apiService: ApiService,
    deliveryId: Int,
    newStatus: String,
    pickupTime: String? = null,
    deliveryTime: String? = null,
    navController: NavController,
    onUpdate: (Delivery) -> Unit
) {
    println("Updating delivery $deliveryId to status $newStatus with pickupTime: $pickupTime, deliveryTime: $deliveryTime")
    val deliveryUpdate = DeliveryUpdate(status = newStatus, pickup_time = pickupTime, delivery_time = deliveryTime)
    apiService.updateDelivery(deliveryId, deliveryUpdate).enqueue(object : Callback<Delivery> {
        override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
            if (response.isSuccessful) {
                response.body()?.let { newDelivery ->
                    println("Status succesvol bijgewerkt naar $newStatus: $newDelivery")
                    onUpdate(newDelivery)
                } ?: println("Response body is null")
            } else {
                println("Fout bij het updaten van de status: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Delivery>, t: Throwable) {
            println("Netwerkfout bij het updaten van de status: ${t.message}")
        }
    })
}

// Helper-functie om de levering te annuleren
fun cancelDelivery(
    apiService: ApiService,
    deliveryId: Int,
    navController: NavController
) {
    apiService.cancelDelivery(deliveryId).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("Levering succesvol geannuleerd!")
                navController.popBackStack()
            } else {
                println("Fout bij het annuleren: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Netwerkfout bij het annuleren: ${t.message}")
        }
    })
}