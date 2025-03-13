package com.example.quickdropapp.composables.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.WhiteSmoke

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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WhiteSmoke),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(containerColor = WhiteSmoke)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pakket #${packageId}",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Omschrijving: ${packageItem.description ?: "Geen omschrijving"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${packageItem.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreenSustainable,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("trackPackage/$packageId")
                        },
                        modifier = Modifier
                            .background(GreenSustainable.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
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
                            .background(DarkGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
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
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
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
}