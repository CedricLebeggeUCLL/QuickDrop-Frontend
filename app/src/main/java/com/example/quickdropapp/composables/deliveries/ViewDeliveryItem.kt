package com.example.quickdropapp.composables.deliveries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

fun getStatusAlias(status: String?): Pair<String, Color> = when (status?.uppercase()) {
    "ASSIGNED" -> "Klaar voor Ophalen" to Color(0xFF2196F3)
    "PICKED_UP" -> "Opgehaald" to Color(0xFFFF9800)
    "DELIVERED" -> "Bezorgd" to Color(0xFF4CAF50)
    else -> "Onbekend" to Color.Gray
}

@Composable
fun DeliveryItem(delivery: Delivery, navController: NavController, userId: Int) {
    val (statusAlias, statusColor) = getStatusAlias(delivery.status)
    val dropoffCity = delivery.dropoffAddress?.city ?: "Onbekende stad"
    val title = "Naar $dropoffCity"

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 400)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = GreenSustainable.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    navController.navigate("deliveryInfo/${delivery.id}")
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Schaduw verwijderd
        ) {
            Row(
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
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = GreenSustainable,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statusAlias,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = statusColor,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(
                    onClick = {
                        navController.navigate("trackingDeliveries/$userId?deliveryId=${delivery.id}")
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(GreenSustainable.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Track",
                        tint = GreenSustainable,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}