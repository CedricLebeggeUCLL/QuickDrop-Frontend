package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun HelpSupportScreen(navController: NavController, userId: Int) {
    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Sleek header met gradiënt, exact zoals ViewPackagesScreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GreenSustainable.copy(alpha = 0.15f),
                                Color(0xFF2E7D32).copy(alpha = 0.4f),
                                GreenSustainable.copy(alpha = 0.2f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Help & Support",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Beheer je ondersteuning",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Veelgestelde Vragen",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FAQItem("Hoe volg ik mijn pakket?", "Gebruik de 'Track'-functie op de homepage.")
                        FAQItem("Kan ik mijn adres wijzigen?", "Ja, ga naar Instellingen om je adres te updaten.")
                        FAQItem("Wat als mijn pakket vertraagd is?", "Neem contact op met support via onderstaande gegevens.")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contactinformatie",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("E-mail: support@quickdropapp.com", fontSize = 16.sp, color = DarkGreen.copy(alpha = 0.8f))
                        Text("Telefoon: +32 123 456 789", fontSize = 16.sp, color = DarkGreen.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(question, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DarkGreen)
        Text(answer, fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.7f))
    }
}