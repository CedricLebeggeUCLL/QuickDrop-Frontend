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
import java.text.SimpleDateFormat
import java.util.*

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

    // Verbeterde datumformattering
    fun formatDateTime(dateTime: String?): String {
        if (dateTime.isNullOrEmpty()) return "Niet beschikbaar"

        // Lijst van mogelijke invoerformaten
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        )

        // Stel UTC in voor formaten met 'Z'
        inputFormats[0].timeZone = TimeZone.getTimeZone("UTC")

        // Uitvoerformaat
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        // Probeer elk invoerformaat
        for (inputFormat in inputFormats) {
            try {
                val date = inputFormat.parse(dateTime)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                // Ga verder naar het volgende formaat
            }
        }
        return "Niet beschikbaar"
    }

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
                    text = packageData?.description ?: "Levering",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    modifier = Modifier.weight(1f)
                )
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
            Spacer(modifier = Modifier.height(8.dp))
            // Status badge
            Box(
                modifier = Modifier
                    .background(
                        color = getStatusColor(delivery.status),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = delivery.status?.replaceFirstChar { it.uppercase() } ?: "Onbekend",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Afleveradres
            Text(
                text = "Afleveradres: ${packageData?.dropoffAddress?.let { "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}" } ?: "Onbekend"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Datum en tijd
            Text(
                text = "Opgehaald: ${formatDateTime(delivery.pickup_time)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Text(
                text = "Afgeleverd: ${formatDateTime(delivery.delivery_time)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = DarkGreen.copy(alpha = 0.8f)
            )
        }
    }
}