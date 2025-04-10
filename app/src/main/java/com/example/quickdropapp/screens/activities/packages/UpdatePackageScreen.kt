package com.example.quickdropapp.screens.activities.packages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.quickdropapp.composables.forms.ModernFormField
import com.example.quickdropapp.models.Address
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.packages.PackageRequest
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun UpdatePackageScreen(navController: NavController, packageId: Int) {
    var packageItem by remember { mutableStateOf<Package?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("pending") }
    var actionType by remember { mutableStateOf("send") }
    var category by remember { mutableStateOf("package") }
    var size by remember { mutableStateOf("medium") }
    var pickupAddress by remember { mutableStateOf(Address()) }
    var dropoffAddress by remember { mutableStateOf(Address()) }

    // Error states for required fields
    var descriptionError by remember { mutableStateOf(false) }
    var pickupAddressError by remember { mutableStateOf(false) }
    var dropoffAddressError by remember { mutableStateOf(false) }

    val apiService = RetrofitClient.create(LocalContext.current)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    // Dynamische tekst gebaseerd op category
    val itemTypeText = when (category) {
        "package" -> "pakket"
        "food" -> "eten"
        "drink" -> "drinken"
        else -> "pakket"
    }

    fun log(message: String) {
        println("UpdatePackageScreen: $message")
    }

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
                        actionType = pkg.action_type
                        category = pkg.category
                        size = pkg.size
                        pickupAddress = pkg.pickupAddress?.copy() ?: Address()
                        dropoffAddress = pkg.dropoffAddress?.copy() ?: Address()
                    }
                } else {
                    errorMessage = "Fout bij laden pakket: ${response.message()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Package>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    val isAddressEditable = status in listOf("pending", "assigned")
    val showDeleteButton = status in listOf("pending", "assigned")

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        log("Back button clicked, executing double popBackStack")
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                log("Only one back stack entry was present after first pop")
                            }
                        } else {
                            log("No previous back stack entry found")
                        }
                    }
                ) {
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
                    text = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Bijwerken",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
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
                } else if (packageItem == null) {
                    Text(
                        text = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} niet gevonden",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        text = "Werk de details van $itemTypeText #$packageId bij",
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
                                    imageVector = Icons.Filled.Inventory,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Details van het $itemTypeText",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            ModernFormField(
                                value = description,
                                onValueChange = { description = it },
                                label = "Beschrijving van het $itemTypeText",
                                placeholder = "Bijv. Boeken of Kleding",
                                icon = Icons.Filled.Description,
                                modifier = Modifier.fillMaxWidth(),
                                isError = descriptionError
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Status",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                                        fontSize = 16.sp,
                                        color = DarkGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SubdirectoryArrowRight,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Actie",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (actionType) {
                                            "send" -> "Verzenden"
                                            "receive" -> "Ontvangen"
                                            else -> "Verzenden"
                                        },
                                        fontSize = 16.sp,
                                        color = DarkGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                        color = DarkGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Expand,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Grootte",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (size) {
                                            "small" -> "Klein"
                                            "medium" -> "Medium"
                                            "large" -> "Groot"
                                            else -> "Medium"
                                        },
                                        fontSize = 16.sp,
                                        color = DarkGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
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
                                isEditable = isAddressEditable,
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
                                isEditable = isAddressEditable,
                                isError = dropoffAddressError
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Reset error states and message
                            descriptionError = false
                            pickupAddressError = false
                            dropoffAddressError = false
                            errorMessage = null

                            // Validate required fields
                            if (description.isBlank()) {
                                descriptionError = true
                            }
                            if (pickupAddress.street_name.isBlank() || pickupAddress.house_number.isBlank() || pickupAddress.postal_code.isBlank()) {
                                pickupAddressError = true
                            }
                            if (dropoffAddress.street_name.isBlank() || dropoffAddress.house_number.isBlank() || dropoffAddress.postal_code.isBlank()) {
                                dropoffAddressError = true
                            }

                            // Check if there are any errors
                            if (descriptionError || pickupAddressError || dropoffAddressError) {
                                errorMessage = "Vul alle verplichte velden correct in:"
                                if (descriptionError) errorMessage += "\n- Beschrijving van het $itemTypeText"
                                if (pickupAddressError) errorMessage += "\n- ${if (actionType == "send") "Vertrekpunt" else "Ophaallocatie"} (straat, huisnummer, postcode)"
                                if (dropoffAddressError) errorMessage += "\n- Bestemming (straat, huisnummer, postcode)"
                            } else {
                                // Proceed with update if all fields are valid
                                val updateRequest = PackageRequest(
                                    user_id = packageItem?.user_id ?: 0,
                                    description = description,
                                    pickup_address = pickupAddress.copy(country = "Belgium"),
                                    dropoff_address = dropoffAddress.copy(country = "Belgium"),
                                    action_type = actionType,
                                    category = category,
                                    size = size,
                                    status = status
                                )
                                apiService.updatePackage(packageId, updateRequest).enqueue(object : Callback<Package> {
                                    override fun onResponse(call: Call<Package>, response: Response<Package>) {
                                        if (response.isSuccessful) {
                                            successMessage = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} succesvol bijgewerkt!"
                                            log("Update successful, popping back stack once")
                                            navController.popBackStack()
                                        } else {
                                            errorMessage = "Bijwerken mislukt: ${response.message()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<Package>, t: Throwable) {
                                        errorMessage = "Netwerkfout: ${t.message}"
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
                                contentDescription = "Opslaan",
                                tint = SandBeige,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Bijwerken",
                                color = SandBeige,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (showDeleteButton) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                apiService.deletePackage(packageId).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            successMessage = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} succesvol verwijderd!"
                                            log("Delete successful, executing double popBackStack")
                                            navController.popBackStack()
                                            if (navController.previousBackStackEntry != null) {
                                                navController.popBackStack()
                                            } else {
                                                log("Only one back stack entry was present after first pop")
                                            }
                                        } else {
                                            errorMessage = "Verwijderen mislukt: ${response.message()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        errorMessage = "Netwerkfout bij verwijderen: ${t.message}"
                                    }
                                })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Verwijderen",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${itemTypeText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Verwijderen",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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
}