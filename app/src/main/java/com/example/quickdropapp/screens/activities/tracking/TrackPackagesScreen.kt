package com.example.quickdropapp.screens.activities.tracking

import android.Manifest
import android.os.Looper
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
import com.example.quickdropapp.composables.tracking.TrackingPackageCard
import com.example.quickdropapp.models.packages.Package
import com.example.quickdropapp.models.tracking.TrackingInfo
import com.example.quickdropapp.network.LocationUpdate
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TrackPackagesScreen(navController: NavController, userId: Int) {
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var selectedPackageId by remember { mutableStateOf<Int?>(null) }
    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldPoll by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val apiService = RetrofitClient.create(LocalContext.current)

    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Request location permission if not granted
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Fetch packages for the user
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

    // Poll tracking info for the selected package
    LaunchedEffect(selectedPackageId) {
        selectedPackageId?.let { packageId ->
            Log.d("TrackPackages", "Start polling for packageId: $packageId")
            shouldPoll = true
            while (shouldPoll) {
                apiService.trackPackage(packageId).enqueue(object : Callback<TrackingInfo> {
                    override fun onResponse(call: Call<TrackingInfo>, response: Response<TrackingInfo>) {
                        if (response.isSuccessful) {
                            val info = response.body()
                            if (info != null) {
                                Log.d("Tracking", "Nieuwe tracking info: lat=${info.currentLocation.lat}, lng=${info.currentLocation.lng}")
                                trackingInfo = info
                                shouldPoll = info.status == "in_transit"
                            } else {
                                Log.e("Tracking", "Geen tracking info ontvangen")
                                errorMessage = "Geen tracking informatie beschikbaar"
                                shouldPoll = false
                            }
                        } else {
                            Log.e("Tracking", "Fout bij ophalen tracking: ${response.code()}")
                            errorMessage = "Fout bij ophalen tracking: ${response.code()}"
                            shouldPoll = false
                        }
                    }

                    override fun onFailure(call: Call<TrackingInfo>, t: Throwable) {
                        Log.e("Tracking", "Netwerkfout: ${t.message}")
                        errorMessage = "Netwerkfout: ${t.message}"
                        shouldPoll = false
                    }
                })
                if (shouldPoll) delay(5000)
            }
        }
    }

    // Update courier location periodically
    DisposableEffect(selectedPackageId) {
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null && locationPermissionState.status.isGranted) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    apiService.updateCourierLocation(2, LocationUpdate(latLng.latitude, latLng.longitude))
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("LocationUpdate", "Location updated successfully")
                                } else {
                                    Log.e("LocationUpdate", "Error updating location: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("LocationUpdate", "Network error: ${t.message}")
                            }
                        })
                }
            }
        }

        if (selectedPackageId != null &&
            packages.find { it.id == selectedPackageId }?.status == "in_transit" &&
            locationPermissionState.status.isGranted
        ) {
            val locationRequest = LocationRequest.Builder(5000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()
            locationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        }

        onDispose {
            locationClient.removeLocationUpdates(callback)
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
                    .padding(bottom = 32.dp) // Extra statische padding aan de onderkant
            ) {
                // Header for the bottom sheet
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

                // Package list or empty state
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
                        modifier = Modifier.padding(bottom = 32.dp) // Extra padding onder de pakketten
                    ) {
                        items(filteredPackages) { pkg ->
                            TrackingPackageCard(pkg, isSelected = selectedPackageId == pkg.id) {
                                Log.d("TrackPackages", "Pakket geselecteerd: ${pkg.id}")
                                selectedPackageId = pkg.id
                                scope.launch {
                                    try {
                                        scaffoldState.bottomSheetState.hide()
                                        Log.d("TrackPackages", "Bottom sheet verborgen")
                                    } catch (e: Exception) {
                                        Log.e("TrackPackages", "Fout bij verbergen bottom sheet: ${e.message}")
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
                                    Log.d("TrackPackages", "Bottom sheet geopend")
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
                // Header
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
                                trackingInfo?.currentLocation?.let {
                                    LatLng(it.lat, it.lng)
                                } ?: LatLng(52.3676, 4.9041),
                                14f
                            )
                        }

                        LaunchedEffect(trackingInfo) {
                            trackingInfo?.let { info ->
                                Log.d("MapUpdate", "Kaart bijwerken naar: lat=${info.currentLocation.lat}, lng=${info.currentLocation.lng}")
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(info.currentLocation.lat, info.currentLocation.lng),
                                        15f
                                    ),
                                    durationMs = 1000
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(isMyLocationEnabled = false)
                            ) {
                                trackingInfo?.let { info ->
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(info.currentLocation.lat, info.currentLocation.lng)
                                        ),
                                        title = "Pakket Locatie",
                                        snippet = "Huidige positie van pakket"
                                    )
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