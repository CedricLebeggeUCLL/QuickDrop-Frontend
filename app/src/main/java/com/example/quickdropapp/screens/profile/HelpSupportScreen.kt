package com.example.quickdropapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.support.FAQItem
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
            // Header met terug-knop en titel
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
                        contentDescription = "Terug", // Dutch for "Back"
                        tint = GreenSustainable,
                        modifier = Modifier
                            .size(32.dp)
                            .background(SandBeige.copy(alpha = 0.2f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Help & Ondersteuning", // Dutch for "Help & Support"
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Hoofdinhoud
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "FAQ's en Ondersteuning", // Dutch for "FAQ's and Support Contact"
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // FAQ-sectie
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Ensures the card takes available space and allows scrolling
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Veelgestelde Vragen", // Dutch for "Frequently Asked Questions"
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxHeight() // Ensures LazyColumn uses available height
                        ) {
                            items(
                                listOf(
                                    Pair("Hoe kan ik mijn pakket volgen?", "Gebruik de 'Track'-functie op de activeiten pagina."),
                                    Pair("Kan ik mijn profiel gegevens wijzigen?", "Ja, ga naar Instellingen om je gegevens bij te werken."),
                                    Pair("Welk voertuig is nodig voor een klein pakket?", "Een fiets of scooter is geschikt voor kleine pakketten."),
                                    Pair("Welk voertuig is nodig voor een middelgroot pakket?", "Middelgrote pakketten vereisen een auto of kleine bestelwagen."),
                                    Pair("Welk voertuig is nodig voor een groot pakket?", "Grote pakketten vereisen een bestelwagen of vrachtwagen."),
                                    Pair("Wat zijn de voorwaarden voor het verzenden van pakketten?", "Pakketten moeten stevig verpakt zijn en mogen geen verboden items bevatten (bijv. gevaarlijke stoffen).")
                                )
                            ) { (question, answer) ->
                                FAQItem(question = question, answer = answer)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Contactinformatie-sectie
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contactinformatie", // Dutch for "Contact Information"
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "E-mail: support@quickdropapp.com", // Email remains in English
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Telefoon: +32 123 456 789", // Dutch for "Phone"
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}