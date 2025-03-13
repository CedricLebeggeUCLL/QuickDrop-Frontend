// com.example.quickdropapp.screens/ActivitiesOverviewScreen.kt
package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
    var userRole by remember { mutableStateOf<String?>(null) } // Voor admin/courier controle

    val apiService = RetrofitClient.instance

    // Check of de gebruiker een courier is
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                isCourier = response.isSuccessful && response.body() != null
                userRole = if (isCourier == true) "courier" else "user" // Simuleer admin later
                println("ActivitiesOverviewScreen: User role set to $userRole for userId $userId")
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                isCourier = false
                userRole = "user"
                println("ActivitiesOverviewScreen: Failed to fetch courier status for userId $userId: ${t.message}")
            }
        })
    }

    Scaffold(
        bottomBar = {
            ModernBottomNavigation(navController, userId)
        },
        containerColor = SandBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header met uitlogknop
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activiteiten",
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
            }

            // Sectie: Pakketten (voor alle gebruikers)
            item {
                SectionHeader(title = "Pakketten")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernActionCard(
                            title = "Pakket Versturen",
                            description = "Verstuur duurzaam en snel",
                            icon = Icons.Filled.DoubleArrow,
                            onClick = { navController.navigate("sendPackage/$userId") },
                            containerColor = GreenSustainable
                        )

                        ModernActionCard(
                            title = "Mijn Pakketten",
                            description = "Beheer je verzonden pakketten",
                            icon = Icons.Filled.LocalShipping,
                            onClick = { navController.navigate("viewPackages/$userId") },
                            containerColor = DarkGreen
                        )

                        // Voeg "Word Koerier" toe voor niet-courier-gebruikers
                        if (isCourier == false) {
                            ModernActionCard(
                                title = "Word Koerier",
                                description = "Bezorg en verdien",
                                icon = Icons.Filled.DriveEta,
                                onClick = { navController.navigate("becomeCourier/$userId") },
                                containerColor = DarkGreen
                            )
                        }
                    }
                }
            }

            // Sectie: Leveringen (alleen voor courier en admin)
            if (userRole != null && (userRole == "courier" || userRole == "admin")) {
                item {
                    SectionHeader(title = "Leveringen")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ModernActionCard(
                                title = "Start een Levering",
                                description = "Stel je locatie en radius in",
                                icon = Icons.Filled.DriveEta,
                                onClick = { navController.navigate("startDelivery/$userId") },
                                containerColor = DarkGreen
                            )

                            ModernActionCard(
                                title = "Mijn Leveringen",
                                description = "Bekijk je historie",
                                icon = Icons.Filled.FormatListNumbered,
                                onClick = { navController.navigate("viewDeliveries/$userId") },
                                containerColor = GreenSustainable
                            )
                        }
                    }
                }
            }

            // Sectie: Actieve Activiteiten (voor alle gebruikers)
            item {
                SectionHeader(title = "Actieve Activiteiten")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernActionCard(
                            title = "Track Levering",
                            description = "Volg live je pakket",
                            icon = Icons.Filled.LocationOn,
                            onClick = { navController.navigate("trackDelivery") },
                            containerColor = GreenSustainable
                        )

                        ModernActionCard(
                            title = "Actieve Pakketten/Leveringen",
                            description = "Bekijk je actieve activiteiten",
                            icon = Icons.Filled.LocalActivity,
                            onClick = { navController.navigate("activeActivities/$userId") },
                            containerColor = DarkGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(50.dp)
                .background(GreenSustainable)
                .clip(RoundedCornerShape(1.dp))
        )
    }
}