package com.example.quickdropapp.screens.activities.deliveries

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.forms.AddressInputField
import com.example.quickdropapp.composables.forms.RadiusInputForm
import com.example.quickdropapp.data.RecentFormDataStore
import com.example.quickdropapp.models.*
import com.example.quickdropapp.models.courier.Courier
import com.example.quickdropapp.models.courier.CourierUpdateRequest
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.packages.SearchRequest
import com.example.quickdropapp.models.packages.SearchResponse
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun StartDeliveryScreen(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var startAddress by remember { mutableStateOf(Address()) }
    var destinationAddress by remember { mutableStateOf(Address()) }
    var pickupRadius by remember { mutableStateOf("30.0") }
    var dropoffRadius by remember { mutableStateOf("40.0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var courierId by remember { mutableStateOf<Int?>(null) }
    val packages = remember { mutableStateOf<List<Package>>(emptyList()) }

    // Error states for required fields
    var startAddressError by remember { mutableStateOf(false) }
    var destinationAddressError by remember { mutableStateOf(false) }
    var pickupRadiusError by remember { mutableStateOf(false) }
    var dropoffRadiusError by remember { mutableStateOf(false) }

    val apiService = RetrofitClient.create(LocalContext.current)

    // Fetch courierId when the screen loads
    LaunchedEffect(userId) {
        if (userId <= 0) {
            errorMessage = "Ongeldige user ID: $userId"
            return@LaunchedEffect
        }
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                if (response.isSuccessful) {
                    val courier = response.body()
                    courierId = courier?.id
                    println("StartDeliveryScreen: Fetched courierId: $courierId for userId: $userId")
                } else {
                    errorMessage = "Kon koerier niet vinden: ${response.code()} - ${response.errorBody()?.string() ?: "Geen details"}"
                    println("StartDeliveryScreen: Failed to fetch courier: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                errorMessage = "Netwerkfout bij ophalen koerier: ${t.message}"
                println("StartDeliveryScreen: Network failure fetching courier: ${t.message}")
            }
        })
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
                    text = "Start een Levering",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    scope.launch {
                        RecentFormDataStore.getRecentStartDeliveryDataFlow(context).collect { recentData ->
                            startAddress = recentData.startAddress
                            destinationAddress = recentData.destinationAddress
                            pickupRadius = recentData.pickupRadius
                            dropoffRadius = recentData.dropoffRadius
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Stel je beschikbaarheid en zoekcriteria in",
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
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Vanwaar vertrek je?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            AddressInputField(
                                label = "Vertrekpunt",
                                address = startAddress,
                                onAddressChange = { startAddress = it },
                                isError = startAddressError
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
                                    text = "Wat is je bestemming?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            AddressInputField(
                                label = "Bestemming",
                                address = destinationAddress,
                                onAddressChange = { destinationAddress = it },
                                isError = destinationAddressError
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
                                    imageVector = Icons.Filled.MyLocation,
                                    contentDescription = null,
                                    tint = GreenSustainable,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hoe ver wil je zoeken?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RadiusInputForm(
                                    value = pickupRadius,
                                    onValueChange = { pickupRadius = it.filter { char -> char.isDigit() || char == '.' } },
                                    label = "Ophaalradius (km)",
                                    placeholder = "Bijv. 30.0",
                                    icon = Icons.Filled.LocationSearching,
                                    modifier = Modifier.weight(1f),
                                    isError = pickupRadiusError
                                )
                                RadiusInputForm(
                                    value = dropoffRadius,
                                    onValueChange = { dropoffRadius = it.filter { char -> char.isDigit() || char == '.' } },
                                    label = "Afleverradius (km)",
                                    placeholder = "Bijv. 40.0",
                                    icon = Icons.Filled.LocationSearching,
                                    modifier = Modifier.weight(1f),
                                    isError = dropoffRadiusError
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val buttonScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec = tween(durationMillis = 200)
                    )

                    Button(
                        onClick = {
                            if (courierId == null) {
                                errorMessage = "Koerier-ID niet geladen, probeer opnieuw"
                                return@Button
                            }

                            // Reset error states
                            startAddressError = false
                            destinationAddressError = false
                            pickupRadiusError = false
                            dropoffRadiusError = false
                            errorMessage = null

                            // Validate required fields
                            if (startAddress.street_name.isBlank() || startAddress.house_number.isBlank() ||
                                startAddress.postal_code.isBlank() || startAddress.city.isNullOrBlank() ||
                                startAddress.country.isNullOrBlank()) {
                                startAddressError = true
                            }
                            if (destinationAddress.street_name.isBlank() || destinationAddress.house_number.isBlank() ||
                                destinationAddress.postal_code.isBlank() || destinationAddress.city.isNullOrBlank() ||
                                destinationAddress.country.isNullOrBlank()) {
                                destinationAddressError = true
                            }
                            val pickupRad = pickupRadius.toDoubleOrNull()
                            val dropoffRad = dropoffRadius.toDoubleOrNull()
                            if (pickupRadius.isBlank() || pickupRad == null) {
                                pickupRadiusError = true
                            }
                            if (dropoffRadius.isBlank() || dropoffRad == null) {
                                dropoffRadiusError = true
                            }

                            // Check if there are any errors
                            if (startAddressError || destinationAddressError || pickupRadiusError || dropoffRadiusError) {
                                errorMessage = "Vul alle verplichte velden correct in:"
                                if (startAddressError) errorMessage += "\n- Vertrekpunt (straat, huisnummer, postcode, stad, land)"
                                if (destinationAddressError) errorMessage += "\n- Bestemming (straat, huisnummer, postcode, stad, land)"
                                if (pickupRadiusError) errorMessage += "\n- Ophaalradius (moet een getal zijn)"
                                if (dropoffRadiusError) errorMessage += "\n- Afleverradius (moet een getal zijn)"
                            } else {
                                // Proceed with API calls
                                val updateData = CourierUpdateRequest(
                                    startAddress = startAddress,
                                    destinationAddress = destinationAddress,
                                    pickupRadius = pickupRad!!.toFloat(),
                                    dropoffRadius = dropoffRad!!.toFloat(),
                                    availability = true
                                )

                                val currentEntry = navController.currentBackStackEntry
                                apiService.updateCourier(courierId!!, updateData).enqueue(object : Callback<Courier> {
                                    override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                        if (response.isSuccessful) {
                                            successMessage = "Locatie en radius succesvol ingesteld!"
                                            errorMessage = null
                                            scope.launch {
                                                RecentFormDataStore.saveStartDeliveryData(
                                                    context,
                                                    startAddress,
                                                    destinationAddress,
                                                    pickupRadius,
                                                    dropoffRadius
                                                )
                                                val searchRequest = SearchRequest(
                                                    user_id = userId,
                                                    start_address = startAddress,
                                                    destination_address = destinationAddress,
                                                    pickup_radius = pickupRad,
                                                    dropoff_radius = dropoffRad,
                                                    use_current_as_start = false
                                                )
                                                apiService.searchPackages(searchRequest).enqueue(object : Callback<SearchResponse> {
                                                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                                                        if (response.isSuccessful) {
                                                            val result = response.body()
                                                            val packageList = result?.packages ?: emptyList()
                                                            packages.value = packageList
                                                            currentEntry?.savedStateHandle?.set("packages", packageList)
                                                            if (packageList.isNotEmpty()) {
                                                                navController.navigate("searchPackages/$userId") {
                                                                    popUpTo("startDelivery/$userId") { inclusive = false }
                                                                    launchSingleTop = true
                                                                }
                                                            } else {
                                                                navController.navigate("searchPackages/$userId?noPackages=true") {
                                                                    popUpTo("startDelivery/$userId") { inclusive = false }
                                                                    launchSingleTop = true
                                                                }
                                                            }
                                                        } else {
                                                            navController.navigate("searchPackages/$userId?error=true") {
                                                                popUpTo("startDelivery/$userId") { inclusive = false }
                                                                launchSingleTop = true
                                                            }
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                                                        navController.navigate("searchPackages/$userId?error=true") {
                                                            popUpTo("startDelivery/$userId") { inclusive = false }
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                })
                                            }
                                        } else {
                                            errorMessage = "Fout bij updaten: ${response.code()} - ${response.errorBody()?.string()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<Courier>, t: Throwable) {
                                        errorMessage = "Netwerkfout bij update: ${t.message}"
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
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                        interactionSource = interactionSource
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Zoek Pakketten",
                                tint = SandBeige,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zoek Pakketten",
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
}