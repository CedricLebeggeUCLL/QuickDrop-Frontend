package com.example.quickdropapp.composables.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun PackageItem(
    packageItem: Package,
    navController: NavController,
    userId: Int,
    onDelete: (Int) -> Unit,
    onUpdate: (Int) -> Unit
) {
    val packageId = packageItem.id
    val showTrackButton = packageItem.status != "pending"

    // Status aliassen en kleuren
    val (statusAlias, statusColor) = when (packageItem.status) {
        "pending" -> "In Wachtrij" to Color(0xFF2196F3) // Blauw
        "assigned" -> "Koerier Aangewezen" to Color(0xFFFFC107) // Geel
        "in_transit" -> "Onderweg" to Color(0xFFFF9800) // Oranje
        "delivered" -> "Bezorgd" to Color(0xFF4CAF50) // Groen
        else -> "Onbekend" to Color.Gray
    }

    // Ontvanger of Ophaallocatie extraheren uit description gebaseerd op action_type
    val description = packageItem.description ?: ""
    val labelText = if (packageItem.action_type == "send") "Ontvanger" else "Ophaallocatie"
    val detailText = if (packageItem.action_type == "send") {
        description.split(" - ").getOrNull(1)?.split(", ")?.getOrNull(0)?.replace("Ontvanger: ", "") ?: "Onbekende ontvanger"
    } else {
        description.split(" - ").getOrNull(1)?.split(", ")?.getOrNull(0)?.replace("Ophaallocatie: ", "") ?: "Onbekende locatie"
    }

    // Titel gebaseerd op dropoff stad
    val dropoffCity = packageItem.dropoffAddress?.city ?: "Onbekende stad"
    val title = "Levering naar $dropoffCity"

    // Adresformaten met stad uit pickupAddress en dropoffAddress
    val pickupAddress = packageItem.pickupAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres"
    val dropoffAddress = packageItem.dropoffAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres"

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
                onUpdate(packageId)
                navController.navigate("updatePackage/$packageId")
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SandBeige.copy(alpha = 0.9f), SandBeige.copy(alpha = 0.5f))
                    )
                )
                .padding(16.dp)
        ) {
            // Titel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ontvanger of Ophaallocatie (gefixeerd gebaseerd op action_type)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$labelText: $detailText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    text = "Van: $pickupAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
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
                    text = "Naar: $dropoffAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status en track-knop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = statusAlias,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showTrackButton) {
                    IconButton(
                        onClick = { navController.navigate("trackPackages/$userId") },
                        modifier = Modifier
                            .background(GreenSustainable.copy(alpha = 0.1f), RoundedCornerShape(50))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Track Pakket",
                            tint = GreenSustainable
                        )
                    }
                }
            }
        }
    }
}