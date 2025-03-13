package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.models.User
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
        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()
                    val role = user?.role ?: "user"

                    apiService.getPackagesByUserId(userId).enqueue(object : Callback<List<Package>> {
                        override fun onResponse(call: Call<List<Package>>, response: Response<List<Package>>) {
                            if (response.isSuccessful) {
                                packages = response.body()?.filter { it.status == "delivered" } ?: emptyList()
                            }
                            isLoading = false
                        }

                        override fun onFailure(call: Call<List<Package>>, t: Throwable) {
                            errorMessage = "Fout bij laden pakketten: ${t.message}"
                            isLoading = false
                        }
                    })

                    if (role == "courier" || role == "admin") {
                        apiService.getCourierDeliveries(userId).enqueue(object : Callback<List<Delivery>> {
                            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                                if (response.isSuccessful) {
                                    deliveries = response.body()?.filter { it.status == "delivered" } ?: emptyList()
                                }
                            }

                            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                                errorMessage = "Fout bij laden leveringen: ${t.message}"
                            }
                        })
                    }
                } else {
                    errorMessage = "Fout bij laden gebruiker"
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
                .background(SandBeige)
        ) {
            // Sleek header met gradiënt, exact zoals ViewPackagesScreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GreenSustainable.copy(alpha = 0.15f),
                                Color(0xFF2E7D32).copy(alpha = 0.4f),
                                GreenSustainable.copy(alpha = 0.2f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Geschiedenis",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Beheer je geschiedenis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    packages.isEmpty() && deliveries.isEmpty() -> {
                        Text(
                            text = "Geen geschiedenis gevonden",
                            color = DarkGreen,
                            style = MaterialTheme.typography.bodyLarge,
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
                                        "Pakketgeschiedenis",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(packages) { pkg ->
                                    PackageItem(pkg)
                                }
                            }
                            if (deliveries.isNotEmpty() && (user?.role == "courier" || user?.role == "admin")) {
                                item {
                                    Text(
                                        "Leveringsgeschiedenis",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(deliveries) { delivery ->
                                    DeliveryItem(delivery)
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
fun PackageItem(pkg: Package) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pakket #${pkg.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Status: ${pkg.status}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Datum: ${pkg.created_at ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DeliveryItem(delivery: Delivery) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
                    text = "Status: ${delivery.status}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = "Datum: ${delivery.delivery_time ?: "Onbekend"}",
                    fontSize = 14.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}