package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welkom bij QuickDrop",
            style = MaterialTheme.typography.displayLarge,
            color = GreenSustainable,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Duurzame, hyperlokale bezorging van pakketten en eten",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "Inloggen",
                style = MaterialTheme.typography.titleLarge,
                color = SandBeige
            )
        }
    }
}