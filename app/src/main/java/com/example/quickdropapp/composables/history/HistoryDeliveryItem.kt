package com.example.quickdropapp.composables.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HistoryDeliveryItem(
    delivery: Delivery,
    navController: NavController,
    userId: Int
) {
    var packageData by remember { mutableStateOf<Package?>(null) }
    val apiService = RetrofitClient.create(LocalContext.current)

    LaunchedEffect(delivery.package_id) {
        apiService.getPackageById(delivery.package_id).enqueue(object : Callback<Package> {
            override fun onResponse(call: Call<Package>, response: Response<Package>) {
                if (response.isSuccessful) {
                    packageData = response.body()
                }
            }

            override fun onFailure(call: Call<Package>, t: Throwable) {
                // Geen foutmelding, adres blijft "Onbekend"
            }
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Levering #${delivery.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Pakket ID: ${delivery.package_id}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Status: ${delivery.status?.replaceFirstChar { it.uppercase() } ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Opgehaald: ${delivery.pickup_time ?: "Niet opgehaald"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Afgeleverd: ${delivery.delivery_time ?: "Niet afgeleverd"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Afleveradres: ${packageData?.dropoffAddress?.let { "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}" } ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
            IconButton(
                onClick = { navController.navigate("trackingDeliveries/$userId?deliveryId=${delivery.id}") },
                modifier = Modifier
                    .background(GreenSustainable.copy(alpha = 0.1f), CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Track Levering",
                    tint = GreenSustainable
                )
            }
        }
    }
}