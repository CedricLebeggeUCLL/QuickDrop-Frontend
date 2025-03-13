package com.example.quickdropapp.screens.activities.packages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.AddressInputField // Nieuwe import
import com.example.quickdropapp.models.*
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SendPackageScreen(navController: NavController, userId: Int) {
    var recipientName by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf(Address()) }
    var dropoffAddress by remember { mutableStateOf(Address()) }
    var packageDescription by remember { mutableStateOf("") }
    var packageWeight by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    val apiService = RetrofitClient.instance

    // Log de userId voor debugging
    LaunchedEffect(userId) {
        println("Received userId: $userId")
    }

    Scaffold(
        containerColor = SandBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
                .verticalScroll(rememberScrollState())
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
                    text = "Nieuw Pakket",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Placeholder voor balans
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitel met instructie
                Text(
                    text = "Vul de details in om je pakket duurzaam te versturen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formuliervelden in een Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp, RoundedCornerShape(16.dp), clip = false),
                    colors = CardDefaults.cardColors(containerColor = SandBeige),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Ontvanger Naam
                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = { Text("Naam van de ontvanger") },
                            placeholder = { Text("Bijv. Jan Jansen") },
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

                        // Ophaaladres
                        AddressInputField(
                            label = "Adres waar je het ophaalt",
                            address = pickupAddress,
                            onAddressChange = { pickupAddress = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Afleveradres
                        AddressInputField(
                            label = "Adres waar het naartoe gaat",
                            address = dropoffAddress,
                            onAddressChange = { dropoffAddress = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Beschrijving pakket
                        OutlinedTextField(
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            label = { Text("Beschrijving van het pakket") },
                            placeholder = { Text("Bijv. Boeken of Kleding") },
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

                        // Gewicht pakket
                        OutlinedTextField(
                            value = packageWeight,
                            onValueChange = { packageWeight = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Gewicht van het pakket (kg)") },
                            placeholder = { Text("Bijv. 2.5") },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Verstuur knop met gradient en animatie
                Button(
                    onClick = {
                        // Validatie
                        if (recipientName.isBlank()) {
                            errorMessage = "De naam van de ontvanger is verplicht"
                            return@Button
                        }
                        if (pickupAddress.street_name.isBlank() || pickupAddress.house_number.isBlank() || pickupAddress.postal_code.isBlank()) {
                            errorMessage = "Een geldig ophaaladres is verplicht (straat, huisnummer en postcode)"
                            return@Button
                        }
                        if (dropoffAddress.street_name.isBlank() || dropoffAddress.house_number.isBlank() || dropoffAddress.postal_code.isBlank()) {
                            errorMessage = "Een geldig afleveradres is verplicht (straat, huisnummer en postcode)"
                            return@Button
                        }
                        if (packageDescription.isBlank()) {
                            errorMessage = "Een beschrijving van het pakket is verplicht"
                            return@Button
                        }
                        if (packageWeight.isBlank() || packageWeight.toDoubleOrNull() == null) {
                            errorMessage = "Het gewicht van het pakket is verplicht en moet een geldig getal zijn"
                            return@Button
                        }

                        if (userId <= 0) {
                            errorMessage = "Ongeldige gebruikers-ID: $userId"
                            return@Button
                        }

                        // Combineer gegevens in de beschrijving
                        val fullDescription = "$packageDescription - Ontvanger: $recipientName, Gewicht: $packageWeight kg"

                        // Maak PackageRequest object
                        val packageRequest = PackageRequest(
                            user_id = userId,
                            description = fullDescription,
                            pickup_address = pickupAddress,
                            dropoff_address = dropoffAddress
                        )

                        // Log de data voor debugging
                        println("Sending package request: $packageRequest")
                        println("Sending with userId: $userId")

                        // Verstuur naar backend
                        val call = apiService.addPackage(packageRequest)
                        call.enqueue(object : Callback<Package> {
                            override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    successMessage = "Je pakket is succesvol aangemaakt! (ID: ${responseBody?.id ?: "onbekend"})"
                                    errorMessage = null
                                    // Reset form
                                    recipientName = ""
                                    pickupAddress = Address()
                                    dropoffAddress = Address()
                                    packageDescription = ""
                                    packageWeight = ""
                                    // Terug naar HomeScreen
                                    navController.popBackStack()
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Geen details"
                                    errorMessage = "Er ging iets mis: ${response.code()} - ${errorBody}"
                                    successMessage = null
                                    println("Error response: ${response.code()} - ${errorBody}")
                                }
                            }

                            override fun onFailure(call: Call<Package>, t: Throwable) {
                                errorMessage = "Netwerkfout: ${t.message}"
                                successMessage = null
                                println("Failure: ${t.message}")
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(GreenSustainable, DarkGreen)
                            )
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = SandBeige
                    ),
                    enabled = recipientName.isNotBlank() &&
                            pickupAddress.street_name.isNotBlank() &&
                            pickupAddress.house_number.isNotBlank() &&
                            pickupAddress.postal_code.isNotBlank() &&
                            dropoffAddress.street_name.isNotBlank() &&
                            dropoffAddress.house_number.isNotBlank() &&
                            dropoffAddress.postal_code.isNotBlank() &&
                            packageDescription.isNotBlank() &&
                            packageWeight.isNotBlank() &&
                            packageWeight.toDoubleOrNull() != null,
                    interactionSource = interactionSource,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = "Verstuur",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verstuur Pakket",
                            color = SandBeige,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fout- en succesmeldingen onderaan
                successMessage?.let {
                    Text(
                        text = it,
                        color = GreenSustainable,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Extra ruimte aan het einde
            }
        }
    }
}