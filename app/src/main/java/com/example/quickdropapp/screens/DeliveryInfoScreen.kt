package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.quickdropapp.models.Delivery
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
                    text = "Levering Details",
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
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = GreenSustainable)
                    }
                    delivery == null -> {
                        Text(
                            text = "Levering niet gevonden",
                            color = DarkGreen,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        delivery?.let { del ->
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Levering ID: ${del.id}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pakket ID: ${del.package_id}",
                                    fontSize = 16.sp,
                                    color = DarkGreen.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ophaallocatie: [${del.pickupLocation?.joinToString(", ")}]",
                                    fontSize = 16.sp,
                                    color = DarkGreen.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Afleverlocatie: [${del.dropoffLocation?.joinToString(", ")}]",
                                    fontSize = 16.sp,
                                    color = DarkGreen.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Status: ${del.status?.uppercase() ?: "ASSIGNED"}",
                                    fontSize = 16.sp,
                                    color = DarkGreen.copy(alpha = 0.8f)
                                )
                                del.pickupTime?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Opgehaald: $it",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.6f)
                                    )
                                }
                                del.deliveryTime?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Afgeleverd: $it",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.6f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            if (del.status == "assigned") {
                                                updateDeliveryStatus(apiService, deliveryId, "picked_up", navController) { newDelivery ->
                                                    delivery = newDelivery // Update de lokale state
                                                    if (newDelivery.status == "picked_up") {
                                                        navController.popBackStack()
                                                    }
                                                }
                                            } else if (del.status == "picked_up") {
                                                updateDeliveryStatus(apiService, deliveryId, "delivered", navController) { newDelivery ->
                                                    delivery = newDelivery // Update de lokale state
                                                    if (newDelivery.status == "delivered") {
                                                        navController.popBackStack()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = del.status in listOf("assigned", "picked_up"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GreenSustainable,
                                            contentColor = SandBeige
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = when (del.status) {
                                                "assigned" -> "Ophalen"
                                                "picked_up" -> "Afleveren"
                                                else -> "Geen actie"
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            cancelDelivery(apiService, deliveryId, navController)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = SandBeige
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Annuleren",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
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
}

// Helper-functie om de status bij te werken
fun updateDeliveryStatus(
    apiService: ApiService,
    deliveryId: Int,
    newStatus: String,
    navController: NavController,
    onUpdate: (Delivery) -> Unit
) {
    val updatedDelivery = Delivery(id = deliveryId, package_id = 0, courier_id = 0, user_id = null, pickupLocation = emptyList(), dropoffLocation = emptyList(), pickupTime = null, deliveryTime = null, status = newStatus)
    apiService.updateDelivery(deliveryId, updatedDelivery).enqueue(object : Callback<Delivery> {
        override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
            if (response.isSuccessful) {
                response.body()?.let { newDelivery ->
                    println("Status succesvol bijgewerkt naar $newStatus")
                    onUpdate(newDelivery) // Roep de callback aan met de nieuwe delivery
                }
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