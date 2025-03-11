// com.example.quickdropapp.composables/DeliveryItem.kt
package com.example.quickdropapp.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun DeliveryItem(delivery: Delivery, navController: NavController) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 400)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400))
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
                )
                .clickable {
                    navController.navigate("deliveryInfo/${delivery.id}")
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Geen schaduw
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Levering ID: ${delivery.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${delivery.status?.uppercase() ?: "ASSIGNED"}",
                        fontSize = 14.sp,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                    if (delivery.status == "delivered" && delivery.delivery_time != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Afgeleverd: ${delivery.delivery_time}",
                            fontSize = 12.sp,
                            color = DarkGreen.copy(alpha = 0.6f)
                        )
                    } else if (delivery.pickup_time != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opgehaald: ${delivery.pickup_time}",
                            fontSize = 12.sp,
                            color = DarkGreen.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(
                    onClick = { navController.navigate("trackDelivery/${delivery.id}") },
                    modifier = Modifier
                        .size(40.dp)
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