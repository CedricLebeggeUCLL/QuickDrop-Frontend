package com.example.quickdropapp.screens.activities.packages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.AddressInputField
import com.example.quickdropapp.models.Address
import com.example.quickdropapp.models.Package
import com.example.quickdropapp.models.PackageRequest
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UpdatePackageScreen(navController: NavController, packageId: Int) {
    var packageItem by remember { mutableStateOf<Package?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Formulierstatussen
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("pending") }
    var pickupAddress by remember { mutableStateOf(Address()) }
    var dropoffAddress by remember { mutableStateOf(Address()) }

    val apiService = RetrofitClient.instance

    // Animatie voor knop
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    // Laad pakketgegevens
    LaunchedEffect(packageId) {
        if (packageId <= 0) {
            errorMessage = "Ongeldige package ID: $packageId"
            isLoading = false
            return@LaunchedEffect
        }

        apiService.getPackageById(packageId).enqueue(object : Callback<Package> {
            override fun onResponse(call: Call<Package>, response: Response<Package>) {
                if (response.isSuccessful) {
                    packageItem = response.body()
                    packageItem?.let { pkg ->
                        description = pkg.description ?: ""
                        status = pkg.status ?: "pending"
                        pickupAddress = pkg.pickupAddress?.copy() ?: Address()
                        dropoffAddress = pkg.dropoffAddress?.copy() ?: Address()
                    }
                    isLoading = false
                } else {
                    errorMessage = "Fout bij laden pakket: ${response.message()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<Package>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
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
                    text = "Pakket Bijwerken",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                IconButton(
                    onClick = {
                        if (description.isNotEmpty() && status.isNotEmpty()) {
                            val updateRequest = PackageRequest(
                                user_id = packageItem?.user_id ?: 0,
                                description = description,
                                pickup_address = pickupAddress.copy(country = "Belgium"),
                                dropoff_address = dropoffAddress.copy(country = "Belgium"),
                                status = status
                            )
                            apiService.updatePackage(packageId, updateRequest).enqueue(object : Callback<Package> {
                                override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                    if (response.isSuccessful) {
                                        successMessage = "Pakket succesvol bijgewerkt!"
                                        navController.popBackStack()
                                    } else {
                                        errorMessage = "Bijwerken mislukt: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<Package>, t: Throwable) {
                                    errorMessage = "Netwerkfout: ${t.message}"
                                }
                            })
                        } else {
                            errorMessage = "Vul alle verplichte velden in"
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Opslaan",
                        tint = if (isLoading) GreenSustainable.copy(alpha = 0.5f) else GreenSustainable
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = GreenSustainable,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(16.dp)
                    )
                } else {
                    // Subtitel
                    Text(
                        text = "Werk de details van pakket #$packageId bij",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Formulier in een Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(6.dp, RoundedCornerShape(16.dp), clip = false),
                        colors = CardDefaults.cardColors(containerColor = SandBeige),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Omschrijving
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
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

                            // Status
                            OutlinedTextField(
                                value = status,
                                onValueChange = { status = it },
                                label = { Text("Status") },
                                placeholder = { Text("Bijv. pending, shipped") },
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
                                label = "Ophaaladres",
                                address = pickupAddress,
                                onAddressChange = { pickupAddress = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Afleveradres
                            AddressInputField(
                                label = "Afleveradres",
                                address = dropoffAddress,
                                onAddressChange = { dropoffAddress = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opslaan knop met gradient en animatie
                    Button(
                        onClick = {
                            if (description.isNotEmpty() && status.isNotEmpty()) {
                                val updateRequest = PackageRequest(
                                    user_id = packageItem?.user_id ?: 0,
                                    description = description,
                                    pickup_address = pickupAddress.copy(country = "Belgium"),
                                    dropoff_address = dropoffAddress.copy(country = "Belgium"),
                                    status = status
                                )
                                apiService.updatePackage(packageId, updateRequest).enqueue(object : Callback<Package> {
                                    override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                        if (response.isSuccessful) {
                                            successMessage = "Pakket succesvol bijgewerkt!"
                                            navController.popBackStack()
                                        } else {
                                            errorMessage = "Bijwerken mislukt: ${response.message()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<Package>, t: Throwable) {
                                        errorMessage = "Netwerkfout: ${t.message}"
                                    }
                                })
                            } else {
                                errorMessage = "Vul alle verplichte velden in"
                            }
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
                        enabled = !isLoading && description.isNotEmpty() && status.isNotEmpty(),
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
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Opslaan",
                                tint = SandBeige,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Opslaan",
                                color = SandBeige,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fout- en succesmeldingen
                    successMessage?.let {
                        Text(
                            text = it,
                            color = GreenSustainable,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
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
                                .padding(bottom = 8.dp),
                            maxLines = 5
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}