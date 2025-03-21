package com.example.quickdropapp.composables.deliveries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeliveryInfoCard(
    delivery: Delivery,
    onUpdateStatus: (ApiService, Int, String, String?, String?, NavController, (Delivery) -> Unit) -> Unit,
    onCancel: (ApiService, Int, NavController) -> Unit,
    apiService: ApiService,
    navController: NavController,
    onDeliveryUpdated: (Delivery) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = GreenSustainable.copy(alpha = 0.2f), // Subtle border
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // No shadows
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SandBeige.copy(alpha = 0.9f), // Softer start color
                            SandBeige.copy(alpha = 0.5f) // Softer end color
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Levering #${delivery.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pakket ID: ${delivery.package_id}",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGreen.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ophaaladres: Onbekend (ID: ${delivery.pickup_address_id})", // Placeholder
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Afleveradres: Onbekend (ID: ${delivery.dropoff_address_id})", // Placeholder
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status: ${delivery.status?.uppercase() ?: "ASSIGNED"}",
                style = MaterialTheme.typography.bodyMedium,
                color = GreenSustainable
            )
            delivery.pickup_time?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Opgehaald: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f)
                )
            }
            delivery.delivery_time?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Afgeleverd: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedVisibility(
                    visible = delivery.status == "assigned",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    Button(
                        onClick = {
                            println("Ophalen knop geklikt")
                            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            onUpdateStatus(apiService, delivery.id!!, "picked_up", currentTime, null, navController) { newDelivery ->
                                onDeliveryUpdated(newDelivery)
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
                    visible = delivery.status == "picked_up",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    Button(
                        onClick = {
                            println("Afleveren knop geklikt")
                            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            onUpdateStatus(apiService, delivery.id!!, "delivered", null, currentTime, navController) { newDelivery ->
                                onDeliveryUpdated(newDelivery)
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
                    visible = delivery.status != "delivered",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    Button(
                        onClick = {
                            onCancel(apiService, delivery.id!!, navController)
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