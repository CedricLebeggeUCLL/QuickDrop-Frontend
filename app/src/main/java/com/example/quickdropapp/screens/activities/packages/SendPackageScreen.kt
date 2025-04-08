package com.example.quickdropapp.screens.activities.packages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.forms.AddressInputField
import com.example.quickdropapp.composables.forms.LabeledIconTextField
import com.example.quickdropapp.data.RecentFormDataStore
import com.example.quickdropapp.models.Address
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.packages.PackageRequest
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SendPackageScreen(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var recipientName by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf(Address()) }
    var dropoffAddress by remember { mutableStateOf(Address()) }
    var packageDescription by remember { mutableStateOf("") }
    var packageWeight by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Foutstatussen voor elk verplicht veld
    var recipientNameError by remember { mutableStateOf(false) }
    var pickupAddressError by remember { mutableStateOf(false) }
    var dropoffAddressError by remember { mutableStateOf(false) }
    var packageDescriptionError by remember { mutableStateOf(false) }
    var packageWeightError by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    val apiService = RetrofitClient.create(LocalContext.current)

    LaunchedEffect(userId) {
        println("Received userId: $userId")
    }

    Scaffold(containerColor = SandBeige) { paddingValues ->
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
                IconButton(onClick = {
                    scope.launch {
                        RecentFormDataStore.getRecentSendPackageDataFlow(context).collect { recentData ->
                            recipientName = recentData.recipientName
                            pickupAddress = recentData.pickupAddress
                            dropoffAddress = recentData.dropoffAddress
                            packageDescription = recentData.packageDescription
                            packageWeight = recentData.packageWeight
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Restore,
                        contentDescription = "Herstel recente gegevens",
                        tint = GreenSustainable
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vul de details in om je pakket duurzaam te versturen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White, SandBeige.copy(alpha = 0.3f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                        LabeledIconTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = "Naam van de ontvanger",
                            placeholder = "Bijv. Jan Jansen",
                            icon = Icons.Filled.Person,
                            modifier = Modifier.fillMaxWidth(),
                            isError = recipientNameError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White, SandBeige.copy(alpha = 0.3f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            onAddressChange = { pickupAddress = it },
                            isError = pickupAddressError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White, SandBeige.copy(alpha = 0.3f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            onAddressChange = { dropoffAddress = it },
                            isError = dropoffAddressError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White, SandBeige.copy(alpha = 0.3f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                        LabeledIconTextField(
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            label = "Beschrijving van het pakket",
                            placeholder = "Bijv. Boeken of Kleding",
                            icon = Icons.Filled.Description,
                            modifier = Modifier.fillMaxWidth(),
                            isError = packageDescriptionError
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LabeledIconTextField(
                            value = packageWeight,
                            onValueChange = { packageWeight = it.filter { char -> char.isDigit() || char == '.' } },
                            label = "Gewicht van het pakket (kg)",
                            placeholder = "Bijv. 2.5",
                            icon = Icons.Filled.FitnessCenter,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            isError = packageWeightError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Reset foutstatussen
                        recipientNameError = false
                        pickupAddressError = false
                        dropoffAddressError = false
                        packageDescriptionError = false
                        packageWeightError = false
                        errorMessage = null

                        // Validatie van alle verplichte velden
                        if (recipientName.isBlank()) {
                            recipientNameError = true
                        }
                        if (pickupAddress.street_name.isBlank() || pickupAddress.house_number.isBlank() || pickupAddress.postal_code.isBlank()) {
                            pickupAddressError = true
                        }
                        if (dropoffAddress.street_name.isBlank() || dropoffAddress.house_number.isBlank() || dropoffAddress.postal_code.isBlank()) {
                            dropoffAddressError = true
                        }
                        if (packageDescription.isBlank()) {
                            packageDescriptionError = true
                        }
                        if (packageWeight.isBlank() || packageWeight.toDoubleOrNull() == null) {
                            packageWeightError = true
                        }

                        // Controleer of er fouten zijn
                        if (recipientNameError || pickupAddressError || dropoffAddressError || packageDescriptionError || packageWeightError) {
                            errorMessage = "Vul alle verplichte velden correct in:"
                            if (recipientNameError) errorMessage += "\n- Naam van de ontvanger"
                            if (pickupAddressError) errorMessage += "\n- Vertrekpunt (straat, huisnummer, postcode)"
                            if (dropoffAddressError) errorMessage += "\n- Bestemming (straat, huisnummer, postcode)"
                            if (packageDescriptionError) errorMessage += "\n- Beschrijving van het pakket"
                            if (packageWeightError) errorMessage += "\n- Gewicht van het pakket (moet een getal zijn)"
                        } else {
                            // Als alles correct is ingevuld, verstuur het pakket
                            val fullDescription = "$packageDescription - Ontvanger: $recipientName, Gewicht: $packageWeight kg"
                            val packageRequest = PackageRequest(
                                user_id = userId,
                                description = fullDescription,
                                pickup_address = pickupAddress,
                                dropoff_address = dropoffAddress
                            )

                            val call = apiService.addPackage(packageRequest)
                            call.enqueue(object : Callback<Package> {
                                override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                    if (response.isSuccessful) {
                                        successMessage = "Je pakket is succesvol aangemaakt! (ID: ${response.body()?.id ?: "onbekend"})"
                                        errorMessage = null
                                        scope.launch {
                                            RecentFormDataStore.saveSendPackageData(
                                                context,
                                                recipientName,
                                                pickupAddress,
                                                dropoffAddress,
                                                packageDescription,
                                                packageWeight
                                            )
                                            navController.popBackStack()
                                        }
                                    } else {
                                        errorMessage = "Er ging iets mis: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                                        successMessage = null
                                    }
                                }

                                override fun onFailure(call: Call<Package>, t: Throwable) {
                                    errorMessage = "Netwerkfout: ${t.message}"
                                    successMessage = null
                                }
                            })
                        }
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
                    interactionSource = interactionSource,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
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

                successMessage?.let {
                    Text(
                        text = it,
                        color = GreenSustainable,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 8.dp),
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
                            .padding(horizontal = 8.dp),
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}