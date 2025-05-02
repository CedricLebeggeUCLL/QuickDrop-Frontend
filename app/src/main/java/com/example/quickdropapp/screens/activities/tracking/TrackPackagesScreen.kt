package com.example.quickdropapp.screens.activities.tracking

import android.Manifest
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.R
import com.example.quickdropapp.composables.tracking.TrackingPackageCard
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.tracking.TrackingInfo
import com.example.quickdropapp.network.*
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import com.example.quickdropapp.utils.ApiKeyUtils
import com.example.quickdropapp.utils.MapUtils
import com.example.quickdropapp.utils.PolylineDecoder
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TrackPackagesScreen(navController: NavController, userId: Int) {
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var selectedPackageId by remember { mutableStateOf<Int?>(null) }
    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldPoll by remember { mutableStateOf(false) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var courierLocation by remember { mutableStateOf<LatLng?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val apiService = RetrofitClient.create(LocalContext.current)
    val routesApiService = RetrofitClient.createRoutesApi()
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val apiKey = ApiKeyUtils.getRoutesApiKey(context) ?: "AIzaSyD7S5MDomqTRbvLmdGOkdgveaHUep1IteQ"

    // Vraag locatierechten aan
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Haal de huidige locatie van de koerier op
    DisposableEffect(Unit) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    courierLocation = LatLng(location.latitude, location.longitude)
                    Log.d("CourierLocation", "Huidige locatie: lat=${location.latitude}, lng=${location.longitude}")
                    // Update koerierlocatie naar backend
                    apiService.updateCourierLocation(userId, LocationUpdate(location.latitude, location.longitude))
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("LocationUpdate", "Koerierlocatie succesvol bijgewerkt")
                                } else {
                                    Log.e("LocationUpdate", "Fout bij bijwerken locatie: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("LocationUpdate", "Netwerkfout: ${t.message}")
                            }
                        })
                }
            }
        }

        val locationRequest = LocationRequest.Builder(5000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        try {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, android.os.Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("CourierLocation", "Geen locatierechten: ${e.message}")
            errorMessage = "Locatierechten vereist"
        }

        onDispose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Haal pakketten op voor de gebruiker
    LaunchedEffect(userId) {
        apiService.getPackagesByUserId(userId).enqueue(object : Callback<List<Package>> {
            override fun onResponse(call: Call<List<Package>>, response: Response<List<Package>>) {
                if (response.isSuccessful) {
                    packages = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Fout bij ophalen pakketten: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Package>>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    // Poll trackinginformatie voor het geselecteerde pakket
    LaunchedEffect(selectedPackageId) {
        selectedPackageId?.let { packageId ->
            shouldPoll = true
            while (shouldPoll) {
                apiService.trackPackage(packageId).enqueue(object : Callback<TrackingInfo> {
                    override fun onResponse(call: Call<TrackingInfo>, response: Response<TrackingInfo>) {
                        if (response.isSuccessful) {
                            trackingInfo = response.body()
                            trackingInfo?.let { info ->
                                shouldPoll = info.status in listOf("assigned", "in_transit")
                                // Haal route op op basis van status
                                if (courierLocation != null &&
                                    info.pickupAddress.lat != null && info.pickupAddress.lng != null &&
                                    info.dropoffAddress.lat != null && info.dropoffAddress.lng != null
                                ) {
                                    val origin = Waypoint(
                                        location = Location(
                                            latLng = LatLng(
                                                latitude = courierLocation!!.latitude,
                                                longitude = courierLocation!!.longitude
                                            )
                                        )
                                    )
                                    val destination = when (info.status) {
                                        "assigned" -> Waypoint(
                                            location = Location(
                                                latLng = LatLng(
                                                    latitude = info.pickupAddress.lat,
                                                    longitude = info.pickupAddress.lng
                                                )
                                            )
                                        )
                                        "in_transit" -> Waypoint(
                                            location = Location(
                                                latLng = LatLng(
                                                    latitude = info.dropoffAddress.lat,
                                                    longitude = info.dropoffAddress.lng
                                                )
                                            )
                                        )
                                        else -> null
                                    }

                                    if (destination != null) {
                                        val request = ComputeRoutesRequest(
                                            origin = origin,
                                            destination = destination,
                                            routingPreference = "TRAFFIC_AWARE_OPTIMAL"
                                        )
                                        routesApiService.computeRoutes(request, apiKey)
                                            .enqueue(object : Callback<ComputeRoutesResponse> {
                                                override fun onResponse(
                                                    call: Call<ComputeRoutesResponse>,
                                                    response: Response<ComputeRoutesResponse>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        val routesResponse = response.body()
                                                        if (routesResponse != null && routesResponse.routes.isNotEmpty()) {
                                                            val encodedPolyline = routesResponse.routes[0].polyline.encodedPolyline
                                                            routePoints = PolylineDecoder.decode(encodedPolyline)
                                                            Log.d("TrackPackages", "Route opgehaald: ${routePoints.size} punten")
                                                        } else {
                                                            Log.w("TrackPackages", "Geen route beschikbaar")
                                                            routePoints = emptyList()
                                                        }
                                                    } else {
                                                        Log.e("TrackPackages", "Fout bij ophalen route: ${response.code()}")
                                                        routePoints = emptyList()
                                                    }
                                                }

                                                override fun onFailure(call: Call<ComputeRoutesResponse>, t: Throwable) {
                                                    Log.e("TrackPackages", "Netwerkfout bij route: ${t.message}")
                                                    routePoints = emptyList()
                                                }
                                            })
                                    } else {
                                        routePoints = emptyList()
                                    }
                                } else {
                                    routePoints = emptyList()
                                }
                            } ?: run {
                                errorMessage = "Geen tracking informatie beschikbaar"
                                shouldPoll = false
                            }
                        } else {
                            errorMessage = "Fout bij ophalen tracking: ${response.code()}"
                            shouldPoll = false
                        }
                    }

                    override fun onFailure(call: Call<TrackingInfo>, t: Throwable) {
                        errorMessage = "Netwerkfout: ${t.message}"
                        shouldPoll = false
                    }
                })
                if (shouldPoll) delay(5000)
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Actieve Pakketten",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }

                val filteredPackages = packages.filter { it.status in listOf("assigned", "in_transit", "delivered") }
                if (filteredPackages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Inbox,
                                contentDescription = null,
                                tint = DarkGreen.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Geen actieve pakketten",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = DarkGreen.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Er zijn nog geen actieve pakketten om te volgen.",
                                fontSize = 14.sp,
                                color = DarkGreen.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        items(filteredPackages) { pkg ->
                            TrackingPackageCard(pkg, isSelected = selectedPackageId == pkg.id) {
                                selectedPackageId = pkg.id
                                scope.launch {
                                    try {
                                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                            scaffoldState.bottomSheetState.hide()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("TrackPackages", "Fout bij sluiten bottom sheet: ${e.message}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContainerColor = SandBeige,
        sheetShadowElevation = 8.dp
    ) {
        Scaffold(
            containerColor = SandBeige,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            try {
                                if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            } catch (e: Exception) {
                                Log.e("TrackPackages", "Fout bij openen bottom sheet: ${e.message}")
                            }
                        }
                    },
                    containerColor = GreenSustainable,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(6.dp, CircleShape)
                ) {
                    Icon(Icons.Filled.Inbox, contentDescription = "Pakkettenlijst")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SandBeige)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                        text = "Track Pakketten",
                        color = GreenSustainable,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = GreenSustainable,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                    errorMessage != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                courierLocation ?: LatLng(50.8503, 4.3517), // Default Brussel
                                14f
                            )
                        }

                        // Pas camera aan op basis van status
                        LaunchedEffect(trackingInfo, routePoints, courierLocation) {
                            trackingInfo?.let { info ->
                                if (info.status == "delivered" && info.dropoffAddress.lat != null && info.dropoffAddress.lng != null) {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(info.dropoffAddress.lat, info.dropoffAddress.lng),
                                            15f
                                        ),
                                        durationMs = 1000
                                    )
                                } else if (courierLocation != null && routePoints.isNotEmpty()) {
                                    val boundsBuilder = LatLngBounds.Builder()
                                    routePoints.forEach { boundsBuilder.include(it) }
                                    boundsBuilder.include(courierLocation!!)
                                    if (info.status == "assigned" && info.pickupAddress.lat != null && info.pickupAddress.lng != null) {
                                        boundsBuilder.include(LatLng(info.pickupAddress.lat, info.pickupAddress.lng))
                                    } else if (info.status == "in_transit" && info.dropoffAddress.lat != null && info.dropoffAddress.lng != null) {
                                        boundsBuilder.include(LatLng(info.dropoffAddress.lat, info.dropoffAddress.lng))
                                    }
                                    val bounds = boundsBuilder.build()
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 50),
                                        durationMs = 1000
                                    )
                                } else if (courierLocation != null) {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            courierLocation!!,
                                            15f
                                        ),
                                        durationMs = 1000
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 0.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(
                                    isMyLocationEnabled = locationPermissionState.status.isGranted,
                                    mapType = MapType.NORMAL,
                                    maxZoomPreference = 18f,
                                    minZoomPreference = 10f
                                ),
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = false,
                                    compassEnabled = true,
                                    mapToolbarEnabled = false
                                )
                            ) {
                                trackingInfo?.let { info ->
                                    // Toon koerierlocatie (alleen bij assigned of in_transit)
                                    if (courierLocation != null && info.status != "delivered") {
                                        Marker(
                                            state = MarkerState(position = courierLocation!!),
                                            title = "Koerier Locatie",
                                            snippet = "Huidige positie van koerier",
                                            icon = MapUtils.bitmapDescriptorFromVector(context, R.drawable.ic_courier),
                                            zIndex = 1f
                                        )
                                    }

                                    // Toon ophaal- en afleveradressen
                                    if (info.pickupAddress.lat != null && info.pickupAddress.lng != null) {
                                        Marker(
                                            state = MarkerState(
                                                position = LatLng(info.pickupAddress.lat, info.pickupAddress.lng)
                                            ),
                                            title = "Ophaaladres",
                                            snippet = "Ophaallocatie",
                                            zIndex = 0f
                                        )
                                    }
                                    if (info.dropoffAddress.lat != null && info.dropoffAddress.lng != null) {
                                        Marker(
                                            state = MarkerState(
                                                position = LatLng(info.dropoffAddress.lat, info.dropoffAddress.lng)
                                            ),
                                            title = "Afleveradres",
                                            snippet = "Afleverlocatie",
                                            zIndex = 0f
                                        )
                                    }

                                    // Toon route (alleen bij assigned of in_transit)
                                    if (info.status != "delivered" && routePoints.isNotEmpty()) {
                                        Polyline(
                                            points = routePoints,
                                            color = Color.Blue,
                                            width = 8f,
                                            zIndex = 0f,
                                            geodesic = true
                                        )
                                    }
                                }
                            }
                        }

                        trackingInfo?.let { info ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .animateContentSize(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = GreenSustainable,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "Status: ${info.status.replaceFirstChar { it.uppercase() }}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenSustainable
                                        )
                                    }
                                    Text(
                                        text = "Afhaaladres: ${info.pickupAddress.street_name} ${info.pickupAddress.house_number}${info.pickupAddress.extra_info?.let { ", $it" } ?: ""}, ${info.pickupAddress.postal_code} ${info.pickupAddress.city ?: ""}${info.pickupAddress.country?.let { ", $it" } ?: ""}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f),
                                        lineHeight = 20.sp
                                    )
                                    Text(
                                        text = "Afleveradres: ${info.dropoffAddress.street_name} ${info.dropoffAddress.house_number}${info.dropoffAddress.extra_info?.let { ", $it" } ?: ""}, ${info.dropoffAddress.postal_code} ${info.dropoffAddress.city ?: ""}${info.dropoffAddress.country?.let { ", $it" } ?: ""}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}