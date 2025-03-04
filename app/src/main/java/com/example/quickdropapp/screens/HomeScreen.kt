package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer // Toegevoegd voor scale animatie
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(16.dp)
    ) {
        Text(
            text = "Welkom bij QuickDrop",
            style = MaterialTheme.typography.displayLarge,
            color = GreenSustainable,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Action Cards
        ActionCard(
            title = "Pakket Versturen",
            description = "Stuur een pakket naar een andere gebruiker",
            onClick = { navController.navigate("sendPackage") },
            containerColor = GreenSustainable,
            contentColor = SandBeige
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            title = "Word Koerier",
            description = "Help bij bezorgingen en verdien beloningen",
            onClick = { navController.navigate("becomeCourier") },
            containerColor = DarkGreen,
            contentColor = SandBeige
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            title = "Track Levering",
            description = "Volg je pakket in realtime",
            onClick = { navController.navigate("trackDelivery") },
            containerColor = GreenSustainable,
            contentColor = SandBeige
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            title = "Bekijk Leveringen",
            description = "Bekijk alle leveringen",
            onClick = { navController.navigate("viewDeliveries") },
            containerColor = DarkGreen,
            contentColor = SandBeige
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigatiebar
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp)),
            containerColor = SandBeige,
            contentColor = GreenSustainable
        ) {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("home") },
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = GreenSustainable) },
                label = { Text("Home", color = GreenSustainable) }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("sendPackage") },
                icon = { Icon(Icons.Filled.Send, contentDescription = "Pakket Versturen", tint = GreenSustainable) },
                label = { Text("Pakket", color = GreenSustainable) }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("trackDelivery") },
                icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Track Levering", tint = GreenSustainable) },
                label = { Text("Track", color = GreenSustainable) }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("viewDeliveries") },
                icon = { Icon(Icons.Filled.List, contentDescription = "Bekijk Leveringen", tint = GreenSustainable) },
                label = { Text("Leveringen", color = GreenSustainable) }
            )
        }
    }
}

@Composable
fun ActionCard(title: String, description: String, onClick: () -> Unit, containerColor: Color, contentColor: Color) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200)
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
            .graphicsLayer(scaleX = scale, scaleY = scale) // Vervangen van .scale(scale)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}