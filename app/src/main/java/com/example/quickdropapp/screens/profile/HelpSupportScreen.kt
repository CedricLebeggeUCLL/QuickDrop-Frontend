package com.example.quickdropapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.QuestionAnswer
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable,
                        modifier = Modifier
                            .size(32.dp)
                            .background(SandBeige.copy(alpha = 0.2f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Help & Support",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Beheer je ondersteuning",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Veelgestelde Vragen",
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FAQItem("Hoe volg ik mijn pakket?", "Gebruik de 'Track'-functie op de homepage.")
                        FAQItem("Kan ik mijn adres wijzigen?", "Ja, ga naar Instellingen om je adres te updaten.")
                        FAQItem("Wat als mijn pakket vertraagd is?", "Neem contact op met support.")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contactinformatie",
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "E-mail: support@quickdropapp.com",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Telefoon: +32 123 456 789",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(
            imageVector = Icons.Default.QuestionAnswer,
            contentDescription = "Vraag",
            tint = DarkGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = DarkGreen
            )
            Text(
                answer,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkGreen.copy(alpha = 0.7f)
            )
        }
    }
}