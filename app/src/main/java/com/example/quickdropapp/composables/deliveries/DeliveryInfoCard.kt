package com.example.quickdropapp.composables.deliveries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeliveryInfoCard(
    delivery: Delivery,
    onUpdateStatus: (ApiService, Int, String, String?, String?, NavController, (Delivery) -> Unit) -> Unit,
    onCancel: (ApiService, Int, NavController) -> Unit?,
    apiService: ApiService,
    navController: NavController,
    onDeliveryUpdated: (Delivery) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("nl"))
    val formattedPickupTime = delivery.pickup_time?.let {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)
            dateFormat.format(date)
        } catch (e: Exception) {
            it
        }
    }
    val formattedDeliveryTime = delivery.delivery_time?.let {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)
            dateFormat.format(date)
        } catch (e: Exception) {
            it
        }
    }

    val pickupAddress = delivery.pickupAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres (ID: ${delivery.pickup_address_id})"
    val dropoffAddress = delivery.dropoffAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres (ID: ${delivery.dropoff_address_id})"

    val (statusAlias, statusColor) = getStatusAlias(delivery.status)
    val dropoffCity = delivery.dropoffAddress?.city ?: "Onbekende stad"
    val title = "Levering naar $dropoffCity"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = GreenSustainable.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Schaduw verwijderd
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SandBeige.copy(alpha = 0.95f),
                            SandBeige.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: $statusAlias",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Adresinformatie
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Ophaaladres: $pickupAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Afleveradres: $dropoffAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Tijdinformatie
            formattedPickupTime?.let {
                Text(
                    text = "Opgehaald: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            formattedDeliveryTime?.let {
                Text(
                    text = "Afgeleverd: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actieknoppen
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
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
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
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Afleveren",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                AnimatedVisibility(
                    visible = delivery.status == "assigned",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    OutlinedButton(
                        onClick = {
                            onCancel.invoke(apiService, delivery.id!!, navController)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
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