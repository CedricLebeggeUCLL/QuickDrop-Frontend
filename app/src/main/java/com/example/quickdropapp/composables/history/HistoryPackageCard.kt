package com.example.quickdropapp.composables.history

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun HistoryPackageItem(
    pkg: Package,
    navController: NavController,
    userId: Int
) {
    // Status badge kleur
    fun getStatusColor(status: String?): Color {
        return when (status?.lowercase()) {
            "delivered" -> GreenSustainable
            "in_transit" -> Color(0xFFFFA500) // Oranje
            "pending" -> Color(0xFF808080) // Grijs
            else -> Color(0xFFB0BEC5) // Lichtgrijs voor onbekend
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pkg.description ?: "Pakket",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { navController.navigate("trackPackages/$userId?packageId=${pkg.id}") },
                    modifier = Modifier
                        .background(GreenSustainable.copy(alpha = 0.1f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Track Pakket",
                        tint = GreenSustainable
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Status badge
            Box(
                modifier = Modifier
                    .background(
                        color = getStatusColor(pkg.status),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = pkg.status?.replaceFirstChar { it.uppercase() } ?: "Onbekend",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Afleveradres
            Text(
                text = "Afleveradres: ${pkg.dropoffAddress?.let { "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}" } ?: "Onbekend"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = DarkGreen.copy(alpha = 0.8f)
            )
        }
    }
}