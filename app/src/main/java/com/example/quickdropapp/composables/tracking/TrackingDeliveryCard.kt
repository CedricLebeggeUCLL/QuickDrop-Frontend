package com.example.quickdropapp.composables.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun TrackingDeliveryCard(delivery: Delivery, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .shadow(if (isSelected) 8.dp else 4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) GreenSustainable else Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Levering #${delivery.id}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else DarkGreen
            )
            Text(
                text = "Status: ${delivery.status}",
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.6f) else DarkGreen.copy(alpha = 0.6f)
            )
        }
    }
}