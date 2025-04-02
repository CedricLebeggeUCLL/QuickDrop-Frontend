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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeliveryItem(delivery: Delivery, navController: NavController, userId: Int) {
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
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SandBeige.copy(alpha = 0.9f),
                                SandBeige.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
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
                        Text(
                            text = "Levering #${delivery.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status: ${delivery.status?.uppercase() ?: "ASSIGNED"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = GreenSustainable
                    )
                    if (delivery.status == "delivered" && formattedDeliveryTime != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Afgeleverd: $formattedDeliveryTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkGreen.copy(alpha = 0.6f)
                        )
                    } else if (formattedPickupTime != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opgehaald: $formattedPickupTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkGreen.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        navController.navigate("trackingDeliveries/$userId?deliveryId=${delivery.id}")
                    },
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