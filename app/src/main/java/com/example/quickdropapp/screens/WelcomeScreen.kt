package com.example.quickdropapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun WelcomeScreen(navController: NavController) {
    // Animatie states
    val textScale by rememberInfiniteTransition(label = "textScale").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f, // Subtielere schaling voor een elegantere look
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing), // Langzamere, vloeiendere animatie
            repeatMode = RepeatMode.Reverse
        ), label = "textScaleAnimation"
    )
    val textAlpha by rememberInfiniteTransition(label = "textAlpha").animateFloat(
        initialValue = 0.9f, // Start iets lichter voor een subtieler effect
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "textAlphaAnimation"
    )
    val buttonScale by rememberInfiniteTransition(label = "buttonScale").animateFloat(
        initialValue = 1f,
        targetValue = 1.03f, // Nog subtielere schaling voor een premium look
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing), // Vloeiendere animatie
            repeatMode = RepeatMode.Reverse
        ), label = "buttonScaleAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(horizontal = 32.dp, vertical = 64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welkom bij QuickDrop",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GreenSustainable
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .scale(textScale)
                .alpha(textAlpha)
        )

        Text(
            text = "Duurzame, hyperlokale bezorging\nvan pakketten en eten",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = DarkGreen.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .padding(bottom = 48.dp)
                .alpha(textAlpha)
        )

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // Iets kleiner voor een elegantere look
                .clip(RoundedCornerShape(16.dp)) // Iets minder ronde hoeken voor een strakkere look
                .scale(buttonScale)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp)), // Diepere schaduw voor premium gevoel
            elevation = ButtonDefaults.elevatedButtonElevation(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp), // Extra padding voor verticale centering
                contentAlignment = Alignment.Center // Centraal uitlijnen van tekst
            ) {
                Text(
                    text = "Begin nu", // Korter, krachtiger, professioneler
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold, // Vettere tekst voor impact
                        color = SandBeige,
                        letterSpacing = 1.5.sp // Subtiele letterafstand voor elegantie
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp) // Minder padding voor centrering
                )
            }
        }
    }
}