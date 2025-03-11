package com.example.quickdropapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import java.text.SimpleDateFormat
import java.util.*

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
            // Sleek header with gradient
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GreenSustainable.copy(alpha = 0.15f),
                                Color(0xFF2E7D32).copy(alpha = 0.4f),
                                GreenSustainable.copy(alpha = 0.2f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Levering Details",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
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
                            color = DarkGreen,
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
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    SandBeige.copy(alpha = 0.95f),
                                                    GreenSustainable.copy(alpha = 0.1f),
                                                    Color.White.copy(alpha = 0.9f)
                                                )
                                            )
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Geen schaduw
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Levering ID: ${del.id}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = DarkGreen
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Pakket ID: ${del.package_id}",
                                            fontSize = 16.sp,
                                            color = DarkGreen.copy(alpha = 0.9f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Ophaallocatie: [${del.pickupLocation?.joinToString(", ")}]",
                                            fontSize = 14.sp,
                                            color = DarkGreen.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Afleverlocatie: [${del.dropoffLocation?.joinToString(", ")}]",
                                            fontSize = 14.sp,
                                            color = DarkGreen.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Status: ${del.status?.uppercase() ?: "ASSIGNED"}",
                                            fontSize = 16.sp,
                                            color = DarkGreen.copy(alpha = 0.9f)
                                        )
                                        del.pickupTime?.let {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Opgehaald: $it",
                                                fontSize = 14.sp,
                                                color = DarkGreen.copy(alpha = 0.7f)
                                            )
                                        }
                                        del.deliveryTime?.let {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Afgeleverd: $it",
                                                fontSize = 14.sp,
                                                color = DarkGreen.copy(alpha = 0.7f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            AnimatedVisibility(
                                                visible = del.status == "assigned",
                                                enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                                                exit = fadeOut(animationSpec = tween(durationMillis = 400))
                                            ) {
                                                Button(
                                                    onClick = {
                                                        println("Ophalen knop geklikt")
                                                        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                                        updateDeliveryStatus(apiService, deliveryId, "picked_up", currentTime, null, navController) { newDelivery ->
                                                            delivery = newDelivery
                                                            if (newDelivery.status == "picked_up") {
                                                                navController.popBackStack()
                                                            }
                                                        }
                                                    },
                                                    enabled = true,
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = GreenSustainable,
                                                        contentColor = SandBeige
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(horizontal = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "Ophalen",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                            AnimatedVisibility(
                                                visible = del.status == "picked_up",
                                                enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                                                exit = fadeOut(animationSpec = tween(durationMillis = 400))
                                            ) {
                                                Button(
                                                    onClick = {
                                                        println("Afleveren knop geklikt")
                                                        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                                        updateDeliveryStatus(apiService, deliveryId, "delivered", null, currentTime, navController) { newDelivery ->
                                                            delivery = newDelivery
                                                            if (newDelivery.status == "delivered") {
                                                                navController.popBackStack()
                                                            }
                                                        }
                                                    },
                                                    enabled = true,
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = GreenSustainable,
                                                        contentColor = SandBeige
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(horizontal = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "Afleveren",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                            AnimatedVisibility(
                                                visible = del.status != "delivered",
                                                enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                                                exit = fadeOut(animationSpec = tween(durationMillis = 400))
                                            ) {
                                                Button(
                                                    onClick = {
                                                        cancelDelivery(apiService, deliveryId, navController)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.error,
                                                        contentColor = SandBeige
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(horizontal = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "Annuleren",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
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
        }
    }
}

// Helper-functie om de status bij te werken (ongewijzigd)
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
    val deliveryUpdate = DeliveryUpdate(id = deliveryId, status = newStatus, pickupTime = pickupTime, deliveryTime = deliveryTime)
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

// Helper-functie om de levering te annuleren (ongewijzigd)
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