package com.example.quickdropapp.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.dashboard.AnimatedStatisticsCard
import com.example.quickdropapp.composables.dashboard.DeliveriesBarChartCard
import com.example.quickdropapp.composables.dashboard.DeliveryStatusChart
import com.example.quickdropapp.composables.dashboard.EnhancedHeader
import com.example.quickdropapp.composables.dashboard.PackageBarChartCard
import com.example.quickdropapp.composables.dashboard.PackageStatusChart
import com.example.quickdropapp.composables.nav.FlyoutMenu
import com.example.quickdropapp.composables.nav.ModernBottomNavigation
import com.example.quickdropapp.models.courier.Courier
import com.example.quickdropapp.models.DeliveryStats
import com.example.quickdropapp.models.PackageStats
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
    var isCourier by remember { mutableStateOf<Boolean?>(null) }
    var userRole by remember { mutableStateOf<String?>(null) }
    var packageStats by remember { mutableStateOf<PackageStats?>(null) }
    var deliveryStats by remember { mutableStateOf<DeliveryStats?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val apiService = RetrofitClient.create(LocalContext.current)

    // Fetch user role
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                isCourier = response.isSuccessful && response.body() != null
                userRole = if (isCourier == true) "courier" else "user"
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                isCourier = false
                userRole = "user"
            }
        })
    }

    // Fetch package stats
    LaunchedEffect(userId) {
        apiService.getPackageStats(userId).enqueue(object : Callback<PackageStats> {
            override fun onResponse(call: Call<PackageStats>, response: Response<PackageStats>) {
                if (response.isSuccessful) {
                    packageStats = response.body()
                }
            }

            override fun onFailure(call: Call<PackageStats>, t: Throwable) {
                // Log error or handle failure
            }
        })
    }

    // Fetch delivery stats if user is courier
    if (userRole == "courier" || userRole == "admin") {
        LaunchedEffect(userId) {
            apiService.getDeliveryStats(userId).enqueue(object : Callback<DeliveryStats> {
                override fun onResponse(call: Call<DeliveryStats>, response: Response<DeliveryStats>) {
                    if (response.isSuccessful) {
                        deliveryStats = response.body()
                    }
                }

                override fun onFailure(call: Call<DeliveryStats>, t: Throwable) {
                    // Log error or handle failure
                }
            })
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FlyoutMenu(
                navController = navController,
                userId = userId,
                userRole = userRole,
                onClose = { scope.launch { drawerState.close() } },
                onLogout = onLogout
            )
        },
        content = {
            Scaffold(
                bottomBar = { ModernBottomNavigation(navController, userId) },
                containerColor = SandBeige
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EnhancedHeader(onMenuClick = { scope.launch { drawerState.open() } }, onLogout = onLogout)
                    AnimatedStatisticsCard(userRole = userRole, packageStats = packageStats, deliveryStats = deliveryStats)
                    PackageBarChartCard(packagesPerMonth = packageStats?.shipmentsPerMonth)
                    PackageStatusChart(statusCounts = packageStats?.statusCounts, navController = navController, userId = userId)
                    if (userRole == "courier" || userRole == "admin") {
                        DeliveriesBarChartCard(deliveriesPerMonth = deliveryStats?.deliveriesPerMonth)
                        DeliveryStatusChart(statusCounts = deliveryStats?.statusCounts, navController = navController, userId = userId)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    )
}