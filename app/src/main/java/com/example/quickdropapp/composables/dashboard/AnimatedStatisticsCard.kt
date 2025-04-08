package com.example.quickdropapp.composables.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.DeliveryStats
import com.example.quickdropapp.models.PackageStats
import com.example.quickdropapp.ui.theme.DarkGreen

@Composable
fun AnimatedStatisticsCard(userRole: String?, packageStats: PackageStats?, deliveryStats: DeliveryStats?) {
    var animateTrigger by remember { mutableStateOf(false) }
    val animatedValue by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = { it * it * (3 - 2 * it) })
    )

    LaunchedEffect(Unit) { animateTrigger = true }

    val activeDeliveries = deliveryStats?.statusCounts?.filter {
        it.status in listOf("assigned", "picked_up", "in_transit")
    }?.sumOf { it.count } ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer(alpha = animatedValue, scaleY = animatedValue),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Statistieken",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (userRole == "courier" || userRole == "admin") Arrangement.SpaceBetween else Arrangement.Center
            ) {
                StatItem(label = "Verzonden Pakketten", value = packageStats?.totalSent?.toString() ?: "0", animatedValue)
                if (userRole == "courier" || userRole == "admin") {
                    StatItem(label = "Actieve Leveringen", value = activeDeliveries.toString(), animatedValue)
                }
            }
        }
    }
}