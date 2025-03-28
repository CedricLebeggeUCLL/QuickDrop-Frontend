package com.example.quickdropapp.composables.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun PackageCard(packageItem: Package, onAccept: (Int) -> Unit) {
    val packageId = packageItem.id ?: return

    // Status aliassen en kleuren
    val (statusAlias, statusColor) = when (packageItem.status) {
        "pending" -> "Beschikbaar" to GreenSustainable // Groen voor "Beschikbaar"
        "assigned" -> "Toegewezen" to Color(0xFF2196F3) // Blauw
        "in_transit" -> "Onderweg" to Color(0xFFFF9800) // Oranje
        "delivered" -> "Afgeleverd" to Color(0xFF4CAF50) // Groen
        else -> "Onbekend" to Color.Gray
    }

    // Beschrijving parsen om pakketinhoud, ontvanger en gewicht te extraheren
    val description = packageItem.description ?: ""
    val parts = description.split(" - ")
    val packageContent = parts.getOrNull(0) ?: "Onbekende inhoud"
    val receiverPart = parts.getOrNull(1)?.split(", ")?.getOrNull(0)?.replace("Ontvanger: ", "") ?: "Onbekende ontvanger"
    val weightPart = parts.getOrNull(1)?.split(", ")?.getOrNull(1)?.replace("Gewicht: ", "") ?: "Onbekend gewicht"

    // Titel gebaseerd op ophaal- en afleverstad
    val pickupCity = packageItem.pickupAddress?.city ?: "Onbekend"
    val dropoffCity = packageItem.dropoffAddress?.city ?: "Onbekend"
    val title = "Van $pickupCity naar $dropoffCity"

    // Adresformaten met fallback
    val pickupAddress = packageItem.pickupAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city ?: "Onbekend"}"
    } ?: "Onbekend adres"
    val dropoffAddress = packageItem.dropoffAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city ?: "Onbekend"}"
    } ?: "Onbekend adres"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, SandBeige.copy(alpha = 0.5f))
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

            // Ontvanger
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
                    text = "Ontvanger: $receiverPart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pakketinhoud
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Inhoud: $packageContent",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gewicht
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Scale,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Gewicht: $weightPart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ophaaladres
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = "Ophaallocatie",
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Ophalen: $pickupAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Afleveradres
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = "Afleverlocatie",
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Afleveren: $dropoffAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status
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
                    text = "Status: $statusAlias",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Accepteer-knop bij "pending"
            if (packageItem.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onAccept(packageId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenSustainable,
                        contentColor = SandBeige
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Accepteren",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Accepteer Levering",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}