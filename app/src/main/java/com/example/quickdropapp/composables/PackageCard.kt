// com.example.quickdropapp.composables/PackageCard.kt
package com.example.quickdropapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun PackageCard(packageItem: Package, onAccept: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SandBeige,
            contentColor = DarkGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pakket ID: ${packageItem.id ?: "Onbekend"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Text(
                    text = packageItem.status?.uppercase() ?: "PENDING",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (packageItem.status == "pending") GreenSustainable else DarkGreen.copy(alpha = 0.6f),
                    modifier = Modifier
                        .background(
                            color = if (packageItem.status == "pending") GreenSustainable.copy(alpha = 0.1f) else SandBeige,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Beschrijving: ${packageItem.description ?: "Geen beschrijving"}",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ophaallocatie: Onbekend (ID: ${packageItem.pickup_address_id})", // Placeholder
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Afleverlocatie: Onbekend (ID: ${packageItem.dropoff_address_id})", // Placeholder
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (packageItem.status == "pending") {
                Button(
                    onClick = {
                        packageItem.id?.let { onAccept(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenSustainable,
                        contentColor = SandBeige
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Accepteren",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}