// com.example.quickdropapp.screens/HomeScreen.kt
package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.ModernBottomNavigation
import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Size
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomeScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
    var isCourier by remember { mutableStateOf<Boolean?>(null) }

    val apiService = RetrofitClient.instance

    // Check of de gebruiker een courier is
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                isCourier = response.isSuccessful && response.body() != null
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                isCourier = false
            }
        })
    }

    Scaffold(
        bottomBar = {
            ModernBottomNavigation(navController, userId)
        },
        containerColor = SandBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header met uitlogknop
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QuickDrop Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenSustainable
                )
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .background(GreenSustainable.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Uitloggen",
                        tint = GreenSustainable
                    )
                }
            }

            // Statistische sectie
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Levering Statistieken",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aantal Verzonden Pakketten: 45",
                        fontSize = 16.sp,
                        color = DarkGreen
                    )
                    Text(
                        text = "Actieve Leveringen: 12",
                        fontSize = 16.sp,
                        color = DarkGreen
                    )
                }
            }

            // Staafdiagram (mock)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Verzendingen per Maand",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)) {
                        val barWidth = size.width / 6
                        val barHeightScale = size.height / 50f
                        val data = listOf(10f, 20f, 15f, 25f, 30f, 18f) // Mock data
                        data.forEachIndexed { index, value ->
                            drawRect(
                                color = GreenSustainable,
                                topLeft = Offset(index * barWidth, size.height - value * barHeightScale),
                                size = Size(barWidth * 0.8f, value * barHeightScale)
                            )
                        }
                    }
                }
            }

            // Taartdiagram (mock)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pakket Categorieën",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Canvas(modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)) {
                        val total = 100f
                        val slices = listOf(30f, 40f, 30f) // Mock data (percentages)
                        var startAngle = 0f
                        slices.forEach { slice ->
                            drawArc(
                                color = listOf(GreenSustainable, DarkGreen, Color.Gray)[slices.indexOf(slice)],
                                startAngle = startAngle,
                                sweepAngle = 360f * (slice / total),
                                useCenter = true
                            )
                            startAngle += 360f * (slice / total)
                        }
                    }
                }
            }

            // Placeholder voor extra info
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}