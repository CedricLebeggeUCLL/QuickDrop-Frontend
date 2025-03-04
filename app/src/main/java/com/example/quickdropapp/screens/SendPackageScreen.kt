package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DoubleArrow
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
fun SendPackageScreen(navController: NavController) {
    Scaffold(
        containerColor = SandBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Custom Top Bar (vervangt TopAppBar)
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
                    text = "Nieuw Pakket",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Placeholder voor balans
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitel
                Text(
                    text = "Verstuur duurzaam en snel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formuliervelden
                var recipientName by remember { mutableStateOf("") }
                var recipientAddress by remember { mutableStateOf("") }
                var packageDescription by remember { mutableStateOf("") }
                var packageWeight by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SandBeige),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = { Text("Naam ontvanger") },
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
                            value = recipientAddress,
                            onValueChange = { recipientAddress = it },
                            label = { Text("Adres ontvanger") },
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
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            label = { Text("Beschrijving pakket") },
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
                            value = packageWeight,
                            onValueChange = { packageWeight = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Gewicht (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

                // Verstuur knop
                Button(
                    onClick = {
                        if (recipientName.isNotBlank() && recipientAddress.isNotBlank() && packageDescription.isNotBlank() && packageWeight.isNotBlank()) {
                            println(
                                "Pakket aangemaakt: Naam: $recipientName, Adres: $recipientAddress, " +
                                        "Beschrijving: $packageDescription, Gewicht: $packageWeight kg"
                            )
                            navController.popBackStack() // Terug naar HomeScreen
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                    shape = RoundedCornerShape(16.dp),
                    enabled = recipientName.isNotBlank() && recipientAddress.isNotBlank() && packageDescription.isNotBlank() && packageWeight.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = "Verstuur",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Verstuur Pakket",
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