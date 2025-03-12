// com.example.quickdropapp.screens/ActivitiesOverviewScreen.kt
package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.ModernActionCard
import com.example.quickdropapp.composables.ModernBottomNavigation
import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ActivitiesOverviewScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
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
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activities",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Welkomsttekst
            Text(
                text = "Kies je actie",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = DarkGreen.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Actieknoppen in een schoner grid
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernActionCard(
                    title = "Pakket Versturen",
                    description = "Verstuur duurzaam en snel",
                    icon = Icons.Filled.DoubleArrow,
                    onClick = { navController.navigate("sendPackage/$userId") },
                    containerColor = GreenSustainable
                )

                if (isCourier == null) {
                    CircularProgressIndicator(
                        color = GreenSustainable,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (isCourier == true) {
                    ModernActionCard(
                        title = "Start een Levering",
                        description = "Stel je locatie en radius in",
                        icon = Icons.Filled.DriveEta,
                        onClick = { navController.navigate("startDelivery/$userId") },
                        containerColor = DarkGreen
                    )
                } else {
                    ModernActionCard(
                        title = "Word Koerier",
                        description = "Bezorg en verdien",
                        icon = Icons.Filled.DriveEta,
                        onClick = { navController.navigate("becomeCourier/$userId") },
                        containerColor = DarkGreen
                    )
                }

                ModernActionCard(
                    title = "Track Levering",
                    description = "Volg live je pakket",
                    icon = Icons.Filled.LocationOn,
                    onClick = { navController.navigate("trackDelivery") },
                    containerColor = GreenSustainable
                )
                ModernActionCard(
                    title = "Mijn Leveringen",
                    description = "Bekijk je historie",
                    icon = Icons.Filled.FormatListNumbered,
                    onClick = { navController.navigate("viewDeliveries/$userId") },
                    containerColor = DarkGreen
                )
            }
        }
    }
}