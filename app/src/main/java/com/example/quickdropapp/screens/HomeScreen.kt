package com.example.quickdropapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.models.PackageStats
import com.example.quickdropapp.models.DeliveryStats
import com.example.quickdropapp.models.StatusCount
import com.example.quickdropapp.models.MonthlyCount
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
    var isCourier by remember { mutableStateOf<Boolean?>(null) }
    var userRole by remember { mutableStateOf<String?>(null) }
    var packageStats by remember { mutableStateOf<PackageStats?>(null) }
    var deliveryStats by remember { mutableStateOf<DeliveryStats?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val apiService = RetrofitClient.instance

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
                    InteractiveBarChartCard(shipmentsPerMonth = packageStats?.shipmentsPerMonth)
                    ShipmentStatusChart(statusCounts = packageStats?.statusCounts)
                    if (userRole == "courier" || userRole == "admin") {
                        DeliveriesBarChartCard(deliveriesPerMonth = deliveryStats?.deliveriesPerMonth)
                        DeliveryStatusChart(statusCounts = deliveryStats?.statusCounts)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    )
}

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
fun AnimatedStatisticsCard(userRole: String?, packageStats: PackageStats?, deliveryStats: DeliveryStats?) {
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
                StatItem(label = "Verzonden Pakketten", value = packageStats?.totalSent?.toString() ?: "0", animatedValue)
                if (userRole == "courier" || userRole == "admin") {
                    StatItem(label = "Actieve Leveringen", value = deliveryStats?.totalDeliveries?.toString() ?: "0", animatedValue)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InteractiveBarChartCard(shipmentsPerMonth: List<MonthlyCount>?) {
    val currentDate = java.time.LocalDate.now()
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM ''yy", java.util.Locale.getDefault())

    val months = List(6) { i ->
        val date = currentDate.minusMonths(i.toLong())
        date.format(formatter) // Bijv. "Nov '24"
    }.reversed()

    val data = months.map { label ->
        val date = java.time.LocalDate.parse("01 $label", java.time.format.DateTimeFormatter.ofPattern("dd MMM ''yy"))
        val month = date.monthValue
        val year = date.year
        shipmentsPerMonth?.find { it.month == month && it.year == year }?.count?.toFloat() ?: 0f
    }

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
                val barWidth = size.width / 6
                data.forEachIndexed { index, value ->
                    val barHeight = if (maxValue > 0) (value / maxValue) * size.height * 0.85f else 0f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(GreenSustainable, GreenSustainable.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(index * barWidth + barWidth * 0.1f, size.height - barHeight),
                        size = Size(barWidth * 0.8f, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            months[index],
                            index * barWidth + barWidth / 2,
                            size.height + 40f,
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
fun ShipmentStatusChart(statusCounts: List<StatusCount>?) {
    val active = statusCounts?.find { it.status == "in_transit" }?.count ?: 0
    val pending = statusCounts?.find { it.status == "pending" }?.count ?: 0
    val delivered = statusCounts?.find { it.status == "delivered" }?.count ?: 0

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
                StatusItem(label = "Active", value = active.toString(), color = Color(0xFFFFA726))
                StatusItem(label = "Pending", value = pending.toString(), color = Color(0xFF42A5F5))
                StatusItem(label = "Delivered", value = delivered.toString(), color = Color(0xFF66BB6A))
            }
        }
    }
}

@Composable
fun DeliveryStatusChart(statusCounts: List<StatusCount>?) {
    val assigned = statusCounts?.find { it.status == "assigned" }?.count ?: 0
    val active = statusCounts?.find { it.status == "picked_up" || it.status == "in_transit" }?.count ?: 0
    val delivered = statusCounts?.find { it.status == "delivered" }?.count ?: 0

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
                StatusItem(label = "Assigned", value = assigned.toString(), color = Color(0xFF42A5F5))
                StatusItem(label = "Active", value = active.toString(), color = Color(0xFF66BB6A))
                StatusItem(label = "Delivered", value = delivered.toString(), color = Color(0xFF8BC34A))
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeliveriesBarChartCard(deliveriesPerMonth: List<MonthlyCount>?) {
    val currentDate = java.time.LocalDate.now()
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM ''yy", java.util.Locale.ENGLISH)

    // Genereer de afgelopen 6 maanden
    val months = List(6) { i ->
        val date = currentDate.minusMonths(i.toLong())
        date.format(formatter) // Bijv. "Mar '24"
    }.reversed()

    // Koppel de data aan de gegenereerde maanden
    val data = months.map { label ->
        val date = java.time.LocalDate.parse(
            "01 $label",
            java.time.format.DateTimeFormatter.ofPattern("dd MMM ''yy", java.util.Locale.ENGLISH)
        )
        val month = date.monthValue // Bijv. 3 voor maart
        val year = date.year // Bijv. 2024
        deliveriesPerMonth?.find { it.month == month && it.year == year }?.count?.toFloat() ?: 0f
    }

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
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val barWidth = size.width / 6
                data.forEachIndexed { index, value ->
                    val barHeight = if (maxValue > 0) (value / maxValue) * size.height * 0.85f else 0f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkGreen, DarkGreen.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(index * barWidth + barWidth * 0.1f, size.height - barHeight),
                        size = Size(barWidth * 0.8f, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            months[index],
                            index * barWidth + barWidth / 2,
                            size.height + 40f, // Meer ruimte voor labels
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