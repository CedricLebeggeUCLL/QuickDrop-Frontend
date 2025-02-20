package com.example.quickdropapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.QuickDropAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickDropAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickDropHomeScreen()
                }
            }
        }
    }
}

@Composable
fun QuickDropHomeScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnimation = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            // Logo of Header Animatie
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "QuickDrop Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scaleAnimation.value)
                )
            }

            Text(
                text = "Welkom bij QuickDrop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Duurzame, hyperlokale bezorging van pakketten en eten",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Action Cards
            ActionCard(
                title = "Pakket Versturen",
                description = "Stuur een pakket naar een andere gebruiker",
                onClick = { /* Navigeer naar pakket versturen scherm */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "Word Koerier",
                description = "Help bij bezorgingen en verdien beloningen",
                onClick = { /* Navigeer naar koerier registratie scherm */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "Track Levering",
                description = "Volg je pakket in realtime",
                onClick = { /* Navigeer naar tracking scherm */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "Bekijk Matches",
                description = "Vind beschikbare koeriers voor je pakket",
                onClick = { /* Navigeer naar matching scherm */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer met duurzaamheid thema
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "Samen voor een groener morgen – QuickDrop maakt bezorging duurzaam!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ActionCard(title: String, description: String, onClick: () -> Unit) {
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
            .scale(scale)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}