package com.example.quickdropapp.composables.tracking

import androidx.compose.foundation.background
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
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun TrackingPackageCard(pkg: Package, isSelected: Boolean, onClick: () -> Unit) {
    val pickupCity = pkg.pickupAddress?.city ?: "Onbekend"
    val dropoffCity = pkg.dropoffAddress?.city ?: "Onbekend"
    val statusText = when (pkg.status) {
        "assigned" -> "Toegewezen"
        "in_transit" -> "Onderweg"
        "delivered" -> "Geleverd"
        else -> "Onbekend"
    }

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
                text = "Pakket #${pkg.id}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else DarkGreen
            )
            Text(
                text = pkg.description ?: "Geen beschrijving",
                fontSize = 14.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else DarkGreen.copy(alpha = 0.8f)
            )
            Text(
                text = "Van $pickupCity naar $dropoffCity",
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.6f) else DarkGreen.copy(alpha = 0.6f)
            )
            Text(
                text = "Status: $statusText",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else GreenSustainable
            )
        }
    }
}