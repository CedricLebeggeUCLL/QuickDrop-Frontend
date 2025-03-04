package com.example.quickdropapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun HomeScreen(navController: NavController, onLogout: () -> Unit) {
    Scaffold(
        bottomBar = {
            ModernBottomNavigation(navController)
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
                    text = "QuickDrop",
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
                text = "Duurzaam. Snel. Simpel.",
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
                    description = " Verstuur duurzaam en snel",
                    icon = Icons.Filled.DoubleArrow,
                    onClick = { navController.navigate("sendPackage") },
                    containerColor = GreenSustainable
                )
                ModernActionCard(
                    title = "Word Koerier",
                    description = " Bezorg en verdien",
                    icon = Icons.Filled.DriveEta,
                    onClick = { navController.navigate("becomeCourier") },
                    containerColor = DarkGreen
                )
                ModernActionCard(
                    title = "Track Levering",
                    description = " Volg live je pakket",
                    icon = Icons.Filled.LocationOn,
                    onClick = { navController.navigate("trackDelivery") },
                    containerColor = GreenSustainable
                )
                ModernActionCard(
                    title = "Mijn Leveringen",
                    description = " Bekijk je historie",
                    icon = Icons.Filled.FormatListNumbered,
                    onClick = { navController.navigate("viewDeliveries") },
                    containerColor = DarkGreen
                )
            }
        }
    }
}

@Composable
fun ModernActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    containerColor: Color
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onClick()
                    isHovered = false
                },
                onClickLabel = title
            )
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SandBeige,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SandBeige
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = SandBeige.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ModernBottomNavigation(navController: NavController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SandBeige)
            .padding(8.dp),
        containerColor = SandBeige,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("home", Icons.Filled.Home, "Home"),
            Triple("sendPackage", Icons.Filled.DoubleArrow, "Send"),
            Triple("trackDelivery", Icons.Filled.LocationOn, "Track"),
            Triple("viewDeliveries", Icons.Filled.FormatListNumbered, "Deliveries")
        )
        items.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == route,
                onClick = { navController.navigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = route,
                        tint = if (navController.currentDestination?.route == route) GreenSustainable else DarkGreen.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (navController.currentDestination?.route == route) GreenSustainable else DarkGreen.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            )
        }
    }
}