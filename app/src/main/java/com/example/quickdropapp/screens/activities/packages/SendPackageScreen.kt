package com.example.quickdropapp.screens.activities.packages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.forms.AddressInputField
import com.example.quickdropapp.models.*
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.packages.PackageRequest
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Custom Top Bar
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

                // Groep 1: Ontvangerinformatie
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        SandBeige.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Wie ontvangt je pakket?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ModernFormField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = "Naam van de ontvanger",
                            placeholder = "Bijv. Jan Jansen",
                            icon = Icons.Filled.Person,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Groep 2: Ophaaladres
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        SandBeige.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vanwaar vertrekt je pakket?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AddressInputField(
                            label = "Vertrekpunt",
                            address = pickupAddress,
                            onAddressChange = { pickupAddress = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Groep 3: Afleveradres
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        SandBeige.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Waar stuur je het naartoe?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AddressInputField(
                            label = "Bestemming",
                            address = dropoffAddress,
                            onAddressChange = { dropoffAddress = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Groep 4: Pakketdetails
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        SandBeige.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Wat zit er in je pakket?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ModernFormField(
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            label = "Beschrijving van het pakket",
                            placeholder = "Bijv. Boeken of Kleding",
                            icon = Icons.Filled.Description,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ModernFormField(
                            value = packageWeight,
                            onValueChange = { packageWeight = it.filter { char -> char.isDigit() || char == '.' } },
                            label = "Gewicht van het pakket (kg)",
                            placeholder = "Bijv. 2.5",
                            icon = Icons.Filled.FitnessCenter,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Verstuur knop met gradient en animatie
                Button(
                    onClick = {
                        // Validatie
                        if (recipientName.isBlank()) {
                            errorMessage = "De naam van de ontvanger is verplicht"
                            return@Button
                        }
                        if (pickupAddress.street_name.isBlank() || pickupAddress.house_number.isBlank() || pickupAddress.postal_code.isBlank()) {
                            errorMessage = "Een geldig vertrekpunt is verplicht (straat, huisnummer en postcode)"
                            return@Button
                        }
                        if (dropoffAddress.street_name.isBlank() || dropoffAddress.house_number.isBlank() || dropoffAddress.postal_code.isBlank()) {
                            errorMessage = "Een geldige bestemming is verplicht (straat, huisnummer en postcode)"
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
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
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

@Composable
fun ModernFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    Row(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreenSustainable,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGreen.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = DarkGreen.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                keyboardOptions = keyboardOptions,
                interactionSource = interactionSource
            )
        }
    }
}