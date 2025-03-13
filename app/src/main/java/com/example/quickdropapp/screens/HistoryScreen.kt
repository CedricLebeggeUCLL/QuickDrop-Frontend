package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                        }

                        override fun onFailure(call: Call<List<Package>>, t: Throwable) {
                            // Log error
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
                                // Log error
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Log error
            }
        })
    }

    Scaffold(
        containerColor = SandBeige,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GreenSustainable.copy(alpha = 0.2f), SandBeige)
                        ),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Terug",
                            tint = DarkGreen
                        )
                    }
                    Text(
                        text = "Geschiedenis",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (packages.isEmpty() && deliveries.isEmpty()) {
                Text("Geen geschiedenis beschikbaar", color = DarkGreen, fontSize = 16.sp)
            } else {
                LazyColumn {
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
                            Spacer(modifier = Modifier.height(8.dp))
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
                            Spacer(modifier = Modifier.height(8.dp))
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