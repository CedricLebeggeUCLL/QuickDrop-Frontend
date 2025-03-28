package com.example.quickdropapp.screens.profile

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.auth.User
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HistoryScreen(navController: NavController, userId: Int) {
    var user by remember { mutableStateOf<User?>(null) }
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var deliveries by remember { mutableStateOf<List<Delivery>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        if (userId <= 0) {
            errorMessage = "Ongeldige user ID: $userId"
            isLoading = false
            return@LaunchedEffect
        }

        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()
                    val role = user?.role ?: "user"

                    apiService.getPackagesByUserId(userId).enqueue(object : Callback<List<Package>> {
                        override fun onResponse(call: Call<List<Package>>, response: Response<List<Package>>) {
                            if (response.isSuccessful) {
                                packages = response.body()?.filter { it.status == "delivered" } ?: emptyList()
                            } else {
                                errorMessage = "Fout bij laden pakketten: ${response.message()}"
                            }
                            isLoading = false
                        }

                        override fun onFailure(call: Call<List<Package>>, t: Throwable) {
                            errorMessage = "Netwerkfout bij laden pakketten: ${t.message}"
                            isLoading = false
                        }
                    })

                    if (role == "courier" || role == "admin") {
                        apiService.getCourierDeliveries(userId).enqueue(object : Callback<List<Delivery>> {
                            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                                if (response.isSuccessful) {
                                    deliveries = response.body()?.filter { it.status == "delivered" } ?: emptyList()
                                } else {
                                    errorMessage = "Fout bij laden leveringen: ${response.message()}"
                                }
                            }

                            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                                errorMessage = "Netwerkfout bij laden leveringen: ${t.message}"
                            }
                        })
                    }
                } else {
                    errorMessage = "Fout bij laden gebruiker: ${response.message()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable,
                        modifier = Modifier
                            .size(32.dp)
                            .background(SandBeige.copy(alpha = 0.2f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Geschiedenis",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jouw voltooide geschiedenis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    packages.isEmpty() && deliveries.isEmpty() -> {
                        Text(
                            text = "Geen voltooide geschiedenis gevonden",
                            color = DarkGreen.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            if (packages.isNotEmpty()) {
                                item {
                                    Text(
                                        "Afgeleverde Pakketten",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(packages) { pkg ->
                                    PackageItem(pkg, navController, userId)
                                }
                            }
                            if (deliveries.isNotEmpty() && (user?.role == "courier" || user?.role == "admin")) {
                                item {
                                    Text(
                                        "Afgeleverde Leveringen",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(deliveries) { delivery ->
                                    DeliveryItem(delivery, navController, userId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackageItem(pkg: Package, navController: NavController, userId: Int) {
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
                    text = "Pakket #${pkg.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Beschrijving: ${pkg.description ?: "Geen beschrijving"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Status: ${pkg.status?.replaceFirstChar { it.uppercase() } ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Afleveradres: ${pkg.dropoffAddress?.let { "${it.street_name} ${it.house_number}, ${it.postal_code} ${it.city}" } ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
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
    }
}

@Composable
fun DeliveryItem(delivery: Delivery, navController: NavController, userId: Int) {
    var packageData by remember { mutableStateOf<Package?>(null) }
    val apiService = RetrofitClient.instance

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