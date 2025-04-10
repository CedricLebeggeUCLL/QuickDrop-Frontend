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
import java.util.Locale // Toegevoegd voor Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPackageScreen(
    navController: NavController,
    userId: Int,
    actionType: String, // "send" of "receive"
    category: String // "package", "food", "drink"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pickupAddress by remember { mutableStateOf(Address()) }
    var dropoffAddress by remember { mutableStateOf(Address()) }
    var size by remember { mutableStateOf("medium") }
    var packageDescription by remember { mutableStateOf("") }
    var packageWeight by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") } // Voor "Verzenden"
    var pickupLocationName by remember { mutableStateOf("") } // Voor "Ontvangen"
    var packageHolderName by remember { mutableStateOf("") } // Voor "Ontvangen"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Foutstatussen voor elk verplicht veld
    var pickupAddressError by remember { mutableStateOf(false) }
    var dropoffAddressError by remember { mutableStateOf(false) }
    var packageDescriptionError by remember { mutableStateOf(false) }
    var packageWeightError by remember { mutableStateOf(false) }
    var receiverNameError by remember { mutableStateOf(false) } // Voor "Verzenden"
    var pickupLocationNameError by remember { mutableStateOf(false) } // Voor "Ontvangen"
    var packageHolderNameError by remember { mutableStateOf(false) } // Voor "Ontvangen"

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    val apiService = RetrofitClient.create(LocalContext.current)

    // Dynamische tekst gebaseerd op category
    val itemTypeText = when (category) {
        "package" -> "pakket"
        "food" -> "eten"
        "drink" -> "drinken"
        else -> "pakket"
    }

    LaunchedEffect(userId) {
        println("Received userId: $userId, actionType: $actionType, category: $category")
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
                    text = if (actionType == "send") "Nieuw ${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Verzenden"
                    else "Nieuw ${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Ontvangen",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    scope.launch {
                        RecentFormDataStore.getRecentSendPackageDataFlow(context).collect { recentData ->
                            pickupAddress = recentData.pickupAddress
                            dropoffAddress = recentData.dropoffAddress
                            packageDescription = recentData.packageDescription
                            packageWeight = recentData.packageWeight
                            receiverName = recentData.recipientName
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
                    text = if (actionType == "send") "Vul de details in om je $itemTypeText te verzenden"
                    else "Vul de details in om een $itemTypeText te laten ophalen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pickup-locatie
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
                                text = if (actionType == "send") "Vanwaar vertrekt je $itemTypeText?"
                                else "Waar moet het $itemTypeText opgehaald worden?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AddressInputField(
                            label = if (actionType == "send") "Vertrekpunt" else "Ophaallocatie",
                            address = pickupAddress,
                            onAddressChange = { pickupAddress = it },
                            isError = pickupAddressError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dropoff-locatie
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
                                text = if (actionType == "send") "Waar stuur je het $itemTypeText naartoe?"
                                else "Waar moet het $itemTypeText afgeleverd worden?",
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

                // Categorie en grootte
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
                                imageVector = Icons.Filled.Category,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Type en grootte van het $itemTypeText",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Categorie (alleen weergeven, niet aanpasbaar)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Category,
                                contentDescription = null,
                                tint = GreenSustainable,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Categorie",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkGreen.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (category) {
                                        "package" -> "Pakket"
                                        "food" -> "Eten"
                                        "drink" -> "Drinken"
                                        else -> "Pakket"
                                    },
                                    fontSize = 16.sp,
                                    color = DarkGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grootte selectie
                        var sizeExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = sizeExpanded,
                            onExpandedChange = { sizeExpanded = !sizeExpanded }
                        ) {
                            OutlinedTextField(
                                value = when (size) {
                                    "small" -> "Klein"
                                    "medium" -> "Medium"
                                    "large" -> "Groot"
                                    else -> "Medium"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Grootte") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = sizeExpanded,
                                onDismissRequest = { sizeExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Klein") },
                                    onClick = {
                                        size = "small"
                                        sizeExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Medium") },
                                    onClick = {
                                        size = "medium"
                                        sizeExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Groot") },
                                    onClick = {
                                        size = "large"
                                        sizeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pakketdetails en dynamische velden
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
                                text = "Details van je $itemTypeText",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamische velden op basis van actionType
                        if (actionType == "send") {
                            LabeledIconTextField(
                                value = receiverName,
                                onValueChange = { receiverName = it },
                                label = "Wie ontvangt je $itemTypeText?",
                                placeholder = "Bijv. Jan Jansen",
                                icon = Icons.Filled.Person,
                                modifier = Modifier.fillMaxWidth(),
                                isError = receiverNameError
                            )
                        } else {
                            LabeledIconTextField(
                                value = pickupLocationName,
                                onValueChange = { pickupLocationName = it },
                                label = "Waar moet het $itemTypeText opgehaald worden?",
                                placeholder = "Bijv. Postkantoor Brussel",
                                icon = Icons.Filled.LocationOn,
                                modifier = Modifier.fillMaxWidth(),
                                isError = pickupLocationNameError
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LabeledIconTextField(
                                value = packageHolderName,
                                onValueChange = { packageHolderName = it },
                                label = "Op welke naam staat het $itemTypeText?",
                                placeholder = "Bijv. John Smith",
                                icon = Icons.Filled.Person,
                                modifier = Modifier.fillMaxWidth(),
                                isError = packageHolderNameError
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        LabeledIconTextField(
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            label = "Beschrijving van het $itemTypeText",
                            placeholder = "Bijv. Boeken of Kleding",
                            icon = Icons.Filled.Description,
                            modifier = Modifier.fillMaxWidth(),
                            isError = packageDescriptionError
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LabeledIconTextField(
                            value = packageWeight,
                            onValueChange = { packageWeight = it.filter { char -> char.isDigit() || char == '.' } },
                            label = "Gewicht van het $itemTypeText (kg)",
                            placeholder = "Bijv. 2.5",
                            icon = Icons.Filled.FitnessCenter,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            isError = packageWeightError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bevestig knop
                Button(
                    onClick = {
                        // Reset foutstatussen
                        pickupAddressError = false
                        dropoffAddressError = false
                        packageDescriptionError = false
                        packageWeightError = false
                        receiverNameError = false
                        pickupLocationNameError = false
                        packageHolderNameError = false
                        errorMessage = null

                        // Validatie van alle verplichte velden
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
                        if (actionType == "send" && receiverName.isBlank()) {
                            receiverNameError = true
                        }
                        if (actionType == "receive") {
                            if (pickupLocationName.isBlank()) {
                                pickupLocationNameError = true
                            }
                            if (packageHolderName.isBlank()) {
                                packageHolderNameError = true
                            }
                        }

                        // Controleer of er fouten zijn
                        if (pickupAddressError || dropoffAddressError || packageDescriptionError || packageWeightError || receiverNameError || pickupLocationNameError || packageHolderNameError) {
                            errorMessage = "Vul alle verplichte velden correct in:"
                            if (pickupAddressError) errorMessage += "\n- Ophaallocatie (straat, huisnummer, postcode)"
                            if (dropoffAddressError) errorMessage += "\n- Bestemming (straat, huisnummer, postcode)"
                            if (packageDescriptionError) errorMessage += "\n- Beschrijving van het $itemTypeText"
                            if (packageWeightError) errorMessage += "\n- Gewicht van het $itemTypeText (moet een getal zijn)"
                            if (receiverNameError) errorMessage += "\n- Naam van de ontvanger"
                            if (pickupLocationNameError) errorMessage += "\n- Waar moet het $itemTypeText opgehaald worden?"
                            if (packageHolderNameError) errorMessage += "\n- Op welke naam staat het $itemTypeText?"
                        } else {
                            // Stel de description samen
                            val fullDescription = if (actionType == "send") {
                                "$packageDescription - Ontvanger: $receiverName, Gewicht: $packageWeight kg"
                            } else {
                                "$packageDescription - Ophaallocatie: $pickupLocationName, Naam: $packageHolderName, Gewicht: $packageWeight kg"
                            }

                            // Maak het pakket aan
                            val packageRequest = PackageRequest(
                                user_id = userId,
                                description = fullDescription,
                                pickup_address = pickupAddress,
                                dropoff_address = dropoffAddress,
                                action_type = actionType,
                                category = category,
                                size = size
                            )

                            val call = apiService.addPackage(packageRequest)
                            call.enqueue(object : Callback<Package> {
                                override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                    if (response.isSuccessful) {
                                        successMessage = "Je $itemTypeText is succesvol aangemaakt! (ID: ${response.body()?.id ?: "onbekend"})"
                                        errorMessage = null
                                        scope.launch {
                                            RecentFormDataStore.saveSendPackageData(
                                                context,
                                                receiverName,
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
                            contentDescription = if (actionType == "send") "Verstuur" else "Ontvang",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (actionType == "send") "Verstuur ${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}"
                            else "Ontvang ${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}",
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