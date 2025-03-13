package com.example.quickdropapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.FlyoutMenu
import com.example.quickdropapp.composables.ModernBottomNavigation
import com.example.quickdropapp.models.Courier // Import Courier model
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.graphicsLayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
    var isCourier by remember { mutableStateOf<Boolean?>(null) }
    var userRole by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val apiService = RetrofitClient.instance

    // Fetch user role using the same logic as ActivitiesOverviewScreen
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                isCourier = response.isSuccessful && response.body() != null
                userRole = if (isCourier == true) "courier" else "user"
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                isCourier = false
                userRole = "user" // Fallback to "user" on network failure
            }
        })
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
                    AnimatedStatisticsCard(userRole = userRole)
                    InteractiveBarChartCard() // "Verzendingen per Maand"
                    ShipmentStatusChart()     // New composable for all users under "Verzendingen per Maand"
                    if (userRole == "courier" || userRole == "admin") {
                        DeliveriesBarChartCard()
                        DeliveryStatusChart() // Updated with "assigned", "active", "delivered"
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    )
}

// Rest of the composables remain unchanged
@Composable
fun EnhancedHeader(onMenuClick: () -> Unit, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GreenSustainable.copy(alpha = 0.2f), SandBeige)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = GreenSustainable
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Dashboard",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkGreen,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(2.dp, CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Uitloggen",
                    tint = GreenSustainable
                )
            }
        }
    }
}

@Composable
fun AnimatedStatisticsCard(userRole: String?) {
    var animateTrigger by remember { mutableStateOf(false) }
    val animatedValue by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = { it * it * (3 - 2 * it) })
    )

    LaunchedEffect(Unit) { animateTrigger = true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer(alpha = animatedValue, scaleY = animatedValue),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Levering Statistieken",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (userRole == "courier" || userRole == "admin") Arrangement.SpaceBetween else Arrangement.Center
            ) {
                StatItem(label = "Verzonden Pakketten", value = "45", animatedValue)
                if (userRole == "courier" || userRole == "admin") {
                    StatItem(label = "Actieve Leveringen", value = "12", animatedValue)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, animatedValue: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenSustainable,
            modifier = Modifier.graphicsLayer(alpha = animatedValue)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = DarkGreen.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun InteractiveBarChartCard() {
    val data = listOf(10f, 20f, 15f, 25f, 30f, 18f)
    val labels = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")
    val maxValue = data.maxOrNull() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Verzendingen per Maand",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)) {
                val barWidth = size.width / data.size
                data.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue) * size.height * 0.85f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(GreenSustainable, GreenSustainable.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(index * barWidth + barWidth * 0.1f, size.height - barHeight),
                        size = Size(barWidth * 0.8f, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            labels[index],
                            index * barWidth + barWidth / 2,
                            size.height + 20f,
                            android.graphics.Paint().apply {
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                        drawText(
                            value.toInt().toString(),
                            index * barWidth + barWidth / 2,
                            size.height - barHeight - 10f,
                            android.graphics.Paint().apply {
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShipmentStatusChart() {
    // Mock data (replace with actual data from API if available)
    val activeShipments = 10
    val pendingShipments = 15
    val deliveredShipments = 30

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Verzending Status",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(label = "Active", value = activeShipments.toString(), color = Color(0xFFFFA726))
                StatusItem(label = "Pending", value = pendingShipments.toString(), color = Color(0xFF42A5F5))
                StatusItem(label = "Delivered", value = deliveredShipments.toString(), color = Color(0xFF66BB6A))
            }
        }
    }
}

@Composable
fun DeliveryStatusChart() {
    // Mock data (replace with actual data from API if available)
    val assignedDeliveries = 5
    val activeDeliveries = 12
    val deliveredDeliveries = 45

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Delivery Status",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(label = "Assigned", value = assignedDeliveries.toString(), color = Color(0xFF42A5F5))
                StatusItem(label = "Active", value = activeDeliveries.toString(), color = Color(0xFF66BB6A))
                StatusItem(label = "Delivered", value = deliveredDeliveries.toString(), color = Color(0xFF8BC34A))
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = DarkGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DeliveriesBarChartCard() {
    val data = listOf(5f, 15f, 10f, 20f, 25f, 12f)
    val labels = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")
    val maxValue = data.maxOrNull() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Leveringen per Maand",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)) {
                val barWidth = size.width / data.size
                data.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue) * size.height * 0.85f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkGreen, DarkGreen.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(index * barWidth + barWidth * 0.1f, size.height - barHeight),
                        size = Size(barWidth * 0.8f, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            labels[index],
                            index * barWidth + barWidth / 2,
                            size.height + 20f,
                            android.graphics.Paint().apply {
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                        drawText(
                            value.toInt().toString(),
                            index * barWidth + barWidth / 2,
                            size.height - barHeight - 10f,
                            android.graphics.Paint().apply {
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                    }
                }
            }
        }
    }
}