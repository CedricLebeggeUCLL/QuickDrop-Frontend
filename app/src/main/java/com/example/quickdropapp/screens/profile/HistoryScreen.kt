package com.example.quickdropapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.history.HistoryDeliveryItem
import com.example.quickdropapp.composables.history.HistoryPackageItem
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
    val apiService = RetrofitClient.create(LocalContext.current)

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
                                    HistoryPackageItem(
                                        pkg = pkg,
                                        navController = navController,
                                        userId = userId
                                    )
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
                                    HistoryDeliveryItem(
                                        delivery = delivery,
                                        navController = navController,
                                        userId = userId
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}