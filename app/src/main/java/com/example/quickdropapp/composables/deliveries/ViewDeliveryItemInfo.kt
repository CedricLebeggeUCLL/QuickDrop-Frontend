package com.example.quickdropapp.composables.deliveries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.DeliveryUpdate
import com.example.quickdropapp.models.packages.Package // Voeg dit import toe
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeliveryInfoCard(
    delivery: Delivery,
    apiService: ApiService,
    navController: NavController,
    onDeliveryUpdated: (Delivery) -> Unit
) {
    // State voor de uitklapbare sectie
    var expanded by remember { mutableStateOf(false) }
    var packageDetails by remember { mutableStateOf<Package?>(null) }
    var isLoadingPackage by remember { mutableStateOf(false) }

    // Datumformaten
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val displayFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("nl"))

    val formattedPickupTime = delivery.pickup_time?.let { pickupTime ->
        try {
            val date = isoFormat.parse(pickupTime)
            date?.let { displayFormat.format(it) } ?: "Ongeldige datum"
        } catch (e: Exception) {
            try {
                val fallbackFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = fallbackFormat.parse(pickupTime)
                date?.let { displayFormat.format(it) } ?: "Ongeldige datum"
            } catch (e: Exception) {
                pickupTime
            }
        }
    }

    val formattedDeliveryTime = delivery.delivery_time?.let { deliveryTime ->
        try {
            val date = isoFormat.parse(deliveryTime)
            date?.let { displayFormat.format(it) } ?: "Ongeldige datum"
        } catch (e: Exception) {
            try {
                val fallbackFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = fallbackFormat.parse(deliveryTime)
                date?.let { displayFormat.format(it) } ?: "Ongeldige datum"
            } catch (e: Exception) {
                deliveryTime
            }
        }
    }

    val pickupAddress = delivery.pickupAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres (ID: ${delivery.pickup_address_id})"
    val dropoffAddress = delivery.dropoffAddress?.let {
        "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}"
    } ?: "Onbekend adres (ID: ${delivery.dropoff_address_id})"

    val (statusAlias, statusColor) = getStatusAlias(delivery.status)
    val dropoffCity = delivery.dropoffAddress?.city ?: "Onbekende stad"
    val title = "Levering naar $dropoffCity"

    // Haal pakketdetails op als de sectie wordt geopend
    LaunchedEffect(expanded) {
        if (expanded && packageDetails == null) {
            isLoadingPackage = true
            apiService.getPackageById(delivery.package_id).enqueue(object : Callback<Package> {
                override fun onResponse(call: Call<Package>, response: Response<Package>) {
                    if (response.isSuccessful) {
                        packageDetails = response.body()
                    }
                    isLoadingPackage = false
                }

                override fun onFailure(call: Call<Package>, t: Throwable) {
                    isLoadingPackage = false
                }
            })
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = GreenSustainable.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SandBeige.copy(alpha = 0.95f),
                            SandBeige.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: $statusAlias",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Ophaaladres: $pickupAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                    text = "Afleveradres: $dropoffAddress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Tijdinformatie
            formattedPickupTime?.let {
                Text(
                    text = "Opgehaald: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            formattedDeliveryTime?.let {
                Text(
                    text = "Afgeleverd: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Knop om pakketdetails te tonen/verbergen
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (expanded) "Verberg Pakket Details" else "Toon Pakket Details",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Pakketdetails sectie
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                exit = fadeOut(animationSpec = tween(durationMillis = 400))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    if (isLoadingPackage) {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        packageDetails?.let { pkg ->
                            Text(
                                text = "Pakket Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Beschrijving: ${pkg.description ?: "Geen beschrijving"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGreen.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grootte: ${pkg.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGreen.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Categorie: ${pkg.category}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGreen.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        } ?: Text(
                            text = "Pakketdetails niet beschikbaar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actieknoppen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedVisibility(
                    visible = delivery.status == "assigned",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    Button(
                        onClick = {
                            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            val deliveryUpdate = DeliveryUpdate(status = "picked_up", pickup_time = currentTime)
                            apiService.updateDelivery(delivery.id!!, deliveryUpdate).enqueue(object : Callback<Delivery> {
                                override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { updatedDelivery ->
                                            onDeliveryUpdated(updatedDelivery)
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<Delivery>, t: Throwable) {
                                    // Handle error
                                }
                            })
                        },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenSustainable,
                            contentColor = SandBeige
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Ophalen",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                AnimatedVisibility(
                    visible = delivery.status == "picked_up",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    Button(
                        onClick = {
                            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            val deliveryUpdate = DeliveryUpdate(status = "delivered", delivery_time = currentTime)
                            apiService.updateDelivery(delivery.id!!, deliveryUpdate).enqueue(object : Callback<Delivery> {
                                override fun onResponse(call: Call<Delivery>, response: Response<Delivery>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { updatedDelivery ->
                                            onDeliveryUpdated(updatedDelivery)
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<Delivery>, t: Throwable) {
                                    // Handle error
                                }
                            })
                        },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenSustainable,
                            contentColor = SandBeige
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Afleveren",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                AnimatedVisibility(
                    visible = delivery.status == "assigned",
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400))
                ) {
                    OutlinedButton(
                        onClick = {
                            apiService.cancelDelivery(delivery.id!!).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        navController.popBackStack()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    // Handle error
                                }
                            })
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Annuleren",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}