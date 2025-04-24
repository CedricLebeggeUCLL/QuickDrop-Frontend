package com.example.quickdropapp.composables.tracking

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        "assigned" -> "Moet opgehaald worden"
        "in_transit" -> "Onderweg"
        "delivered" -> "Geleverd"
        else -> "Onbekend"
    }
    val typeText = pkg.category.replaceFirstChar { it.uppercase() }
    val sizeText = pkg.size

    val cardBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) GreenSustainable else Color.White,
        label = "CardBackgroundColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(if (isSelected) 8.dp else 4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = pkg.description ?: "Pakket #${pkg.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else DarkGreen
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Type: $typeText",
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else DarkGreen.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Maat: $sizeText",
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else DarkGreen.copy(alpha = 0.8f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Van $pickupCity",
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else DarkGreen.copy(alpha = 0.8f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isSelected) Color.White.copy(alpha = 0.6f) else DarkGreen.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "naar $dropoffCity",
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else DarkGreen.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "Status: $statusText",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else GreenSustainable
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}