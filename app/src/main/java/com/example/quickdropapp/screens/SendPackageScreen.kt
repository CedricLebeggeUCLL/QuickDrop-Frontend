package com.example.quickdropapp.screens

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
import com.example.quickdropapp.models.Package
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
    var recipientAddress by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var pickupLat by remember { mutableStateOf("") }
    var pickupLng by remember { mutableStateOf("") }
    var dropoffLat by remember { mutableStateOf("") }
    var dropoffLng by remember { mutableStateOf("") }
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

    // Log de userId voor debugging (blijft behouden)
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
                // Subtitel met instructie (geen userId meer)
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
                        OutlinedTextField(
                            value = pickupAddress,
                            onValueChange = { pickupAddress = it },
                            label = { Text("Adres waar je het ophaalt") },
                            placeholder = { Text("Bijv. Hoofdstraat 12, 1000 Brussel") },
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

                        // Ophaalcoördinaten
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = pickupLat,
                                onValueChange = { pickupLat = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Breedtegraad ophaalpunt") },
                                placeholder = { Text("Bijv. 50.8503") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = pickupLng,
                                onValueChange = { pickupLng = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Lengtegraad ophaalpunt") },
                                placeholder = { Text("Bijv. 4.3517") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

                        // Afleveradres
                        OutlinedTextField(
                            value = recipientAddress,
                            onValueChange = { recipientAddress = it },
                            label = { Text("Adres waar het naartoe gaat") },
                            placeholder = { Text("Bijv. Marktplein 5, 2000 Antwerpen") },
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

                        // Aflevercoördinaten
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = dropoffLat,
                                onValueChange = { dropoffLat = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Breedtegraad afleverpunt") },
                                placeholder = { Text("Bijv. 51.2178") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenSustainable,
                                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                                    cursorColor = GreenSustainable,
                                    focusedLabelColor = GreenSustainable
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = dropoffLng,
                                onValueChange = { dropoffLng = it.filter { char -> char.isDigit() || char == '.' || char == '-' } },
                                label = { Text("Lengtegraad afleverpunt") },
                                placeholder = { Text("Bijv. 4.4203") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                        if (pickupAddress.isBlank() || recipientAddress.isBlank()) {
                            errorMessage = "Ophaal- en afleveradres zijn verplicht"
                            return@Button
                        }
                        if (packageDescription.isBlank()) {
                            errorMessage = "Een beschrijving van het pakket is verplicht"
                            return@Button
                        }
                        if (packageWeight.isBlank()) {
                            errorMessage = "Het gewicht van het pakket is verplicht"
                            return@Button
                        }
                        val pickupLatValue = pickupLat.toDoubleOrNull()
                        val pickupLngValue = pickupLng.toDoubleOrNull()
                        val dropoffLatValue = dropoffLat.toDoubleOrNull()
                        val dropoffLngValue = dropoffLng.toDoubleOrNull()

                        if (pickupLatValue == null || pickupLngValue == null || dropoffLatValue == null || dropoffLngValue == null) {
                            errorMessage = "Vul geldige coördinaten in (bijv. 50.8503)"
                            return@Button
                        }

                        if (userId <= 0) {
                            errorMessage = "Ongeldige gebruikers-ID: $userId"
                            return@Button
                        }

                        // Combineer gegevens in de beschrijving
                        val fullDescription = "$packageDescription - Ontvanger: $recipientName, Gewicht: $packageWeight kg"

                        // Maak pakket object
                        val packageData = Package(
                            user_id = userId,
                            description = fullDescription,
                            pickupLocation = listOf(pickupLatValue, pickupLngValue),
                            dropoffLocation = listOf(dropoffLatValue, dropoffLngValue),
                            pickupAddress = pickupAddress,
                            dropoffAddress = recipientAddress,
                            status = "pending" // Status voor delivery search
                        )

                        // Log de data voor debugging
                        println("Sending package: $packageData")
                        println("Sending with userId: $userId")

                        // Verstuur naar backend
                        val call = apiService.addPackage(packageData)
                        call.enqueue(object : Callback<Package> {
                            override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    successMessage = "Je pakket is succesvol aangemaakt! (ID: ${responseBody?.id ?: "onbekend"})"
                                    errorMessage = null
                                    // Reset form
                                    recipientName = ""
                                    recipientAddress = ""
                                    pickupAddress = ""
                                    pickupLat = ""
                                    pickupLng = ""
                                    dropoffLat = ""
                                    dropoffLng = ""
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
                    enabled = recipientName.isNotBlank() && recipientAddress.isNotBlank() && pickupAddress.isNotBlank() &&
                            packageDescription.isNotBlank() && packageWeight.isNotBlank() && pickupLat.isNotBlank() &&
                            pickupLng.isNotBlank() && dropoffLat.isNotBlank() && dropoffLng.isNotBlank(),
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