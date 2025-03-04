package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun BecomeCourierScreen(navController: NavController) {
    // Dummy-gebruikersdata (vervang later door echte ingelogde user)
    val currentUser = "johndoe" // Simulatie van ingelogde gebruiker

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .shadow(4.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable
                    )
                }
                Text(
                    text = "Word Koerier",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Balans
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitel
                Text(
                    text = "Bezorg duurzaam en verdien!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formuliervelden
                var currentLatitude by remember { mutableStateOf("") }
                var currentLongitude by remember { mutableStateOf("") }
                var destinationLatitude by remember { mutableStateOf("") }
                var destinationLongitude by remember { mutableStateOf("") }
                var itsmeCode by remember { mutableStateOf("") } // Simulatie van itsme-verificatie
                var licenseNumber by remember { mutableStateOf("") } // Extra beveiliging

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SandBeige),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Gebruikersinfo (readonly)
                        Text(
                            text = "Gebruikersnaam: $currentUser",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Huidige locatie
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = currentLatitude,
                                onValueChange = { currentLatitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Huidige Latitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = currentLongitude,
                                onValueChange = { currentLongitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Huidige Longitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bestemmingslocatie
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = destinationLatitude,
                                onValueChange = { destinationLatitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Bestemming Latitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = destinationLongitude,
                                onValueChange = { destinationLongitude = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Bestemming Longitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Beveiligingsvelden
                        OutlinedTextField(
                            value = itsmeCode,
                            onValueChange = { itsmeCode = it },
                            label = { Text("Itsme Verificatiecode") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenSustainable,
                                unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                cursorColor = GreenSustainable,
                                focusedLabelColor = GreenSustainable
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = licenseNumber,
                            onValueChange = { licenseNumber = it },
                            label = { Text("Rijbewijsnummer (optioneel)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenSustainable,
                                unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                cursorColor = GreenSustainable,
                                focusedLabelColor = GreenSustainable
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Registreer knop
                Button(
                    onClick = {
                        if (currentLatitude.isNotBlank() && currentLongitude.isNotBlank() &&
                            destinationLatitude.isNotBlank() && destinationLongitude.isNotBlank() &&
                            itsmeCode.isNotBlank()
                        ) {
                            println(
                                "Koerier registratie: " +
                                        "User: $currentUser, " +
                                        "Current Location: [$currentLatitude, $currentLongitude], " +
                                        "Destination: [$destinationLatitude, $destinationLongitude], " +
                                        "Itsme Code: $itsmeCode, " +
                                        "License: ${licenseNumber.ifBlank { "N/A" }}"
                            )
                            navController.popBackStack() // Terug naar HomeScreen
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                    shape = RoundedCornerShape(16.dp),
                    enabled = currentLatitude.isNotBlank() && currentLongitude.isNotBlank() &&
                            destinationLatitude.isNotBlank() && destinationLongitude.isNotBlank() &&
                            itsmeCode.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.BikeScooter,
                            contentDescription = "Registreer",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Registreer als Koerier",
                            color = SandBeige,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}