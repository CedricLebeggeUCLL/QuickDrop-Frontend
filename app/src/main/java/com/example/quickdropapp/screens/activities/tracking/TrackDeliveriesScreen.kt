package com.example.quickdropapp.screens.activities.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.Delivery
import com.example.quickdropapp.models.tracking.DeliveryTrackingInfo
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
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

    val apiService = RetrofitClient.instance

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

    LaunchedEffect(selectedDeliveryId) {
        selectedDeliveryId?.let { deliveryId ->
            apiService.trackDelivery(deliveryId).enqueue(object : Callback<DeliveryTrackingInfo> {
                override fun onResponse(call: Call<DeliveryTrackingInfo>, response: Response<DeliveryTrackingInfo>) {
                    if (response.isSuccessful) {
                        trackingInfo = response.body()
                        errorMessage = null
                    } else {
                        errorMessage = "Fout bij ophalen tracking: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<DeliveryTrackingInfo>, t: Throwable) {
                    errorMessage = "Netwerkfout: ${t.message}"
                }
            })
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deliveries.filter { it.status in listOf("assigned", "picked_up", "delivered") }) { delivery ->
                    DeliveryCard(delivery, selectedDeliveryId == delivery.id) {
                        selectedDeliveryId = delivery.id
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContainerColor = SandBeige,
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
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                scaffoldState.bottomSheetState.hide()
                            } else {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    },
                    containerColor = GreenSustainable,
                    contentColor = Color.White,
                    modifier = Modifier.padding(16.dp)
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
                // Uniforme header
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
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(errorMessage!!, color = Color.Red, fontSize = 16.sp)
                        }
                    }
                    else -> {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                trackingInfo?.currentLocation?.let {
                                    com.google.android.gms.maps.model.LatLng(it.lat, it.lng)
                                } ?: com.google.android.gms.maps.model.LatLng(52.3676, 4.9041),
                                14f
                            )
                        }

                        LaunchedEffect(trackingInfo) {
                            trackingInfo?.let {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        com.google.android.gms.maps.model.LatLng(
                                            it.currentLocation.lat,
                                            it.currentLocation.lng
                                        ),
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
                                            position = com.google.android.gms.maps.model.LatLng(
                                                info.currentLocation.lat,
                                                info.currentLocation.lng
                                            )
                                        ),
                                        title = "Levering Locatie",
                                        snippet = "Huidige locatie van levering #${info.deliveryId}"
                                    )
                                }
                            }
                        }

                        trackingInfo?.let { info ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = SandBeige),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Levering ID: ${info.deliveryId}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GreenSustainable
                                    )
                                    Text(
                                        text = "Status: ${info.status}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GreenSustainable
                                    )
                                    Text(
                                        text = "Geschatte aankomst: ${info.estimatedDelivery}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "Afhaaladres: ${info.pickupAddress.street_name} ${info.pickupAddress.house_number}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "Afleveradres: ${info.dropoffAddress.street_name} ${info.dropoffAddress.house_number}",
                                        fontSize = 14.sp,
                                        color = DarkGreen.copy(alpha = 0.8f)
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

@Composable
fun DeliveryCard(delivery: Delivery, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .shadow(if (isSelected) 8.dp else 4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) GreenSustainable else Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Levering #${delivery.id}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else DarkGreen
            )
            Text(
                text = "Status: ${delivery.status}",
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.6f) else DarkGreen.copy(alpha = 0.6f)
            )
        }
    }
}