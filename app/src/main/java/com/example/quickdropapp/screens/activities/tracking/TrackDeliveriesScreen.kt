package com.example.quickdropapp.screens.activities.tracking

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
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
import com.example.quickdropapp.composables.tracking.TrackingDeliveryCard
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.tracking.DeliveryTrackingInfo
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingDeliveriesScreen(navController: NavController, userId: Int) {
    var deliveries by remember { mutableStateOf<List<Delivery>>(emptyList()) }
    var selectedDeliveryId by remember { mutableStateOf<Int?>(null) }
    var trackingInfo by remember { mutableStateOf<DeliveryTrackingInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val apiService = RetrofitClient.create(LocalContext.current)
    val context = LocalContext.current

    // Fetch deliveries for the user
    LaunchedEffect(userId) {
        apiService.getCourierDeliveries(userId).enqueue(object : Callback<List<Delivery>> {
            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                if (response.isSuccessful) {
                    deliveries = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Fout bij ophalen leveringen: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                errorMessage = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    // Poll tracking info for the selected delivery
    LaunchedEffect(selectedDeliveryId) {
        selectedDeliveryId?.let { deliveryId ->
            Log.d("TrackDeliveries", "Start polling for deliveryId: $deliveryId")
            apiService.trackDelivery(deliveryId).enqueue(object : Callback<DeliveryTrackingInfo> {
                override fun onResponse(call: Call<DeliveryTrackingInfo>, response: Response<DeliveryTrackingInfo>) {
                    if (response.isSuccessful) {
                        val info = response.body()
                        if (info != null) {
                            Log.d("Tracking", "Nieuwe tracking info: lat=${info.currentLocation.lat}, lng=${info.currentLocation.lng}")
                            trackingInfo = info
                            errorMessage = null
                        } else {
                            Log.e("Tracking", "Geen tracking info ontvangen")
                            errorMessage = "Geen tracking informatie beschikbaar"
                        }
                    } else {
                        Log.e("Tracking", "Fout bij ophalen tracking: ${response.code()}")
                        errorMessage = "Fout bij ophalen tracking: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<DeliveryTrackingInfo>, t: Throwable) {
                    Log.e("Tracking", "Netwerkfout: ${t.message}")
                    errorMessage = "Netwerkfout: ${t.message}"
                }
            })
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
                        text = "Actieve Leveringen",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }

                val filteredDeliveries = deliveries.filter { it.status in listOf("assigned", "picked_up", "delivered") }
                if (filteredDeliveries.isEmpty()) {
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
                                text = "Geen actieve leveringen",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = DarkGreen.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Er zijn nog geen actieve leveringen om te volgen.",
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
                        items(filteredDeliveries) { delivery ->
                            TrackingDeliveryCard(delivery, isSelected = selectedDeliveryId == delivery.id) {
                                Log.d("TrackDeliveries", "Levering geselecteerd: ${delivery.id}")
                                selectedDeliveryId = delivery.id
                                scope.launch {
                                    try {
                                        scaffoldState.bottomSheetState.hide()
                                        Log.d("TrackDeliveries", "Bottom sheet verborgen")
                                    } catch (e: Exception) {
                                        Log.e("TrackDeliveries", "Fout bij verbergen bottom sheet: ${e.message}")
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
        sheetShadowElevation = 8.dp,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(50))
            )
        }
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
                                    Log.d("TrackDeliveries", "Bottom sheet geopend")
                                }
                            } catch (e: Exception) {
                                Log.e("TrackDeliveries", "Fout bij openen bottom sheet: ${e.message}")
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
                    Icon(Icons.Filled.Inbox, contentDescription = "Leveringenlijst")
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
                        text = "Track Leveringen",
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
                                        title = "Levering Locatie",
                                        snippet = "Huidige locatie van levering"
                                    )
                                    // Route van pickup naar dropoff
                                    if (info.pickupAddress.lat != null && info.pickupAddress.lng != null &&
                                        info.dropoffAddress.lat != null && info.dropoffAddress.lng != null
                                    ) {
                                        Polyline(
                                            points = listOf(
                                                LatLng(info.pickupAddress.lat, info.pickupAddress.lng),
                                                LatLng(info.dropoffAddress.lat, info.dropoffAddress.lng)
                                            ),
                                            color = Color.Blue,
                                            width = 5f
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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                val address = "${info.dropoffAddress.street_name} ${info.dropoffAddress.house_number}${info.dropoffAddress.extra_info?.let { ", $it" } ?: ""}, ${info.dropoffAddress.postal_code} ${info.dropoffAddress.city ?: ""}${info.dropoffAddress.country?.let { ", $it" } ?: ""}"
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Afleveradres", address)
                                                clipboard.setPrimaryClip(clip)
                                                Log.d("TrackDeliveries", "Adres gekopieerd: $address")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                Icons.Filled.ContentCopy,
                                                contentDescription = "Kopieer adres",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Adres kopiëren")
                                        }
                                        Button(
                                            onClick = {
                                                val address = "${info.dropoffAddress.street_name} ${info.dropoffAddress.house_number}, ${info.dropoffAddress.postal_code} ${info.dropoffAddress.city ?: ""}"
                                                val uri = if (info.dropoffAddress.lat != null && info.dropoffAddress.lng != null) {
                                                    Uri.parse("waze://?ll=${info.dropoffAddress.lat},${info.dropoffAddress.lng}&navigate=yes")
                                                } else {
                                                    Uri.parse("waze://?q=${Uri.encode(address)}&navigate=yes")
                                                }
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                try {
                                                    context.startActivity(intent)
                                                    Log.d("TrackDeliveries", "Waze geopend met adres: $address")
                                                } catch (e: Exception) {
                                                    Log.e("TrackDeliveries", "Fout bij openen Waze: ${e.message}")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                Icons.Filled.Navigation,
                                                contentDescription = "Open in Waze",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Open in Waze")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}