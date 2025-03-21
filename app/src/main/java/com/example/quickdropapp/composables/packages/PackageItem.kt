package com.example.quickdropapp.composables.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PackageItem(
    packageItem: Package,
    navController: NavController,
    onDelete: (Int) -> Unit,
    onUpdate: (Int) -> Unit
) {
    // Safe access to packageItem.id
    val packageId = packageItem.id ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = GreenSustainable.copy(alpha = 0.2f), // Subtle border for modern look
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Transparent to allow gradient background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp // No elevation, shadows removed
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SandBeige.copy(alpha = 0.9f), // Softer start color
                            SandBeige.copy(alpha = 0.5f) // Softer end color
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Package details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
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
                    Text(
                        text = "Pakket #${packageId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = packageItem.description ?: "Geen omschrijving",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f), // Match SendPackageScreen
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${packageItem.status?.replaceFirstChar { it.uppercase() } ?: "Onbekend"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = GreenSustainable,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("trackPackage/$packageId")
                    },
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

                IconButton(
                    onClick = { onUpdate(packageId) },
                    modifier = Modifier
                        .background(DarkGreen.copy(alpha = 0.1f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Update Pakket",
                        tint = DarkGreen
                    )
                }

                IconButton(
                    onClick = {
                        onDelete(packageId)
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Verwijder Pakket",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}