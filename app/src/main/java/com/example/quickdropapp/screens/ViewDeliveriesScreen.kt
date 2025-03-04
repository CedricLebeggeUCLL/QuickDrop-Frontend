package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun ViewDeliveriesScreen(navController: NavController) {
    // Hardcoded testdata gebaseerd op je SQL en Delivery-model
    val deliveries = listOf(
        Delivery(1, 1, 1, "2023-10-01T10:00:00Z", null), // Pakket #1, In Transit
        Delivery(2, 2, 2, "2023-10-01T12:30:00Z", "2023-10-01T14:00:00Z"), // Pakket #2, Delivered
        Delivery(3, 3, 1, "2023-10-01T15:00:00Z", null) // Pakket #3, In Transit
    )

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .shadow(4.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable
                    )
                }
                Text(
                    text = "Mijn Leveringen",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Balans
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitel
                Text(
                    text = "Bekijk je pakketgeschiedenis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lijst van pakketten
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(deliveries) { delivery ->
                        DeliveryItem(
                            delivery = delivery,
                            onTrackClick = { navController.navigate("trackDelivery") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryItem(delivery: Delivery, onTrackClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = SandBeige)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Pakket ID: ${delivery.package_id}", // Package_id als titel
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${if (delivery.delivery_time != null) "Delivered" else "In Transit"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                delivery.pickup_time?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Opgehaald: $it",
                        fontSize = 12.sp,
                        color = DarkGreen.copy(alpha = 0.6f)
                    )
                }
            }
            if (delivery.delivery_time == null) { // Alleen tracken als nog niet geleverd
                IconButton(
                    onClick = onTrackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(GreenSustainable.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Track",
                        tint = GreenSustainable
                    )
                }
            }
        }
    }
}