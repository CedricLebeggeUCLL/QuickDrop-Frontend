package com.example.quickdropapp.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.activities.AnimatedSectionHeader
import com.example.quickdropapp.composables.activities.EnhancedHeaderActivities
import com.example.quickdropapp.composables.nav.FlyoutMenu
import com.example.quickdropapp.composables.nav.ModernActionCard
import com.example.quickdropapp.composables.nav.ModernBottomNavigation
import com.example.quickdropapp.composables.nav.PackageOptionsBottomSheet
import com.example.quickdropapp.models.courier.Courier
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesOverviewScreen(
    navController: NavController,
    userId: Int,
    onLogout: () -> Unit,
    openSheet: Boolean = false
) {
    var isCourier by remember { mutableStateOf<Boolean?>(null) }
    var userRole by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    // State voor navigatie en bottom sheet
    var navigationTrigger by remember { mutableStateOf<String?>(null) }
    var actionTrigger by remember { mutableStateOf<String?>(null) }
    var categoryTrigger by remember { mutableStateOf<String?>(null) }
    var shouldHideBottomSheet by remember { mutableStateOf(false) }

    // Beheer bottom sheet verbergen en navigeren
    LaunchedEffect(shouldHideBottomSheet) {
        if (shouldHideBottomSheet) {
            try {
                scaffoldState.bottomSheetState.expand()
                scaffoldState.bottomSheetState.hide()
                Log.d("BottomSheet", "Bottom sheet succesvol verborgen")
            } catch (e: Exception) {
                Log.e("BottomSheetError", "Fout bij verbergen bottom sheet: ${e.message}")
            }
            shouldHideBottomSheet = false
        }
    }

    // Navigatie uitvoeren wanneer triggers veranderen
    LaunchedEffect(navigationTrigger, actionTrigger, categoryTrigger) {
        if (navigationTrigger != null && actionTrigger != null && categoryTrigger != null) {
            when (navigationTrigger) {
                "sendPackage" -> {
                    Log.d("Navigation", "Navigeren naar sendPackage met userId: $userId, actionType: $actionTrigger, category: $categoryTrigger")
                    try {
                        navController.navigate("sendPackage/$userId/$actionTrigger/$categoryTrigger")
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Fout bij navigeren naar sendPackage: ${e.message}")
                    }
                }
            }
            navigationTrigger = null
            actionTrigger = null
            categoryTrigger = null
        }
    }

    // Bottom sheet automatisch openen als openSheet true is
    LaunchedEffect(openSheet) {
        if (openSheet) {
            scope.launch {
                try {
                    scaffoldState.bottomSheetState.expand()
                    Log.d("BottomSheet", "Bottom sheet succesvol geopend")
                } catch (e: Exception) {
                    Log.e("BottomSheetError", "Fout bij openen bottom sheet: ${e.message}")
                }
            }
        }
    }

    val apiService = RetrofitClient.create(LocalContext.current)

    // Controleer gebruikersrol
    LaunchedEffect(userId) {
        apiService.getCourierByUserId(userId).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                isCourier = response.isSuccessful && response.body() != null
                userRole = if (isCourier == true) "courier" else "user"
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                isCourier = false
                userRole = "user"
            }
        })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FlyoutMenu(
                navController = navController,
                userId = userId,
                userRole = userRole,
                onClose = { scope.launch { drawerState.close() } },
                onLogout = onLogout
            )
        },
        content = {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    PackageOptionsBottomSheet(
                        onConfirmSelection = { actionType, category ->
                            navigationTrigger = "sendPackage"
                            actionTrigger = actionType
                            categoryTrigger = category
                            shouldHideBottomSheet = true
                        }
                    )
                },
                sheetPeekHeight = 0.dp,
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                sheetContainerColor = Color.White,
                modifier = Modifier.shadow(4.dp)
            ) {
                Scaffold(
                    bottomBar = { ModernBottomNavigation(navController, userId) },
                    containerColor = SandBeige
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(SandBeige, Color.White.copy(alpha = 0.8f))
                                )
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EnhancedHeaderActivities(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onLogout = onLogout
                            )
                        }

                        item {
                            AnimatedSectionHeader(title = "Pakketten")
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ModernActionCard(
                                        title = "Start een Verzending",
                                        description = "Pakket verzenden of ontvangen",
                                        icon = Icons.Filled.DoubleArrow,
                                        onClick = {
                                            scope.launch { scaffoldState.bottomSheetState.expand() }
                                        },
                                        containerColor = GreenSustainable
                                    )
                                    ModernActionCard(
                                        title = "Mijn Pakketten",
                                        description = "Beheer je actieve pakketten",
                                        icon = Icons.Filled.LocalShipping,
                                        onClick = { navController.navigate("viewPackages/$userId") },
                                        containerColor = DarkGreen
                                    )
                                }
                            }
                        }

                        if (userRole != null && (userRole == "courier" || userRole == "admin")) {
                            item {
                                AnimatedSectionHeader(title = "Leveringen")
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        ModernActionCard(
                                            title = "Start een Levering",
                                            description = "Stel je locatie en radius in",
                                            icon = Icons.Filled.DriveEta,
                                            onClick = { navController.navigate("startDelivery/$userId") },
                                            containerColor = DarkGreen
                                        )
                                        ModernActionCard(
                                            title = "Mijn Leveringen",
                                            description = "Bekijk je historie",
                                            icon = Icons.Filled.FormatListNumbered,
                                            onClick = { navController.navigate("viewDeliveries/$userId") },
                                            containerColor = GreenSustainable
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            AnimatedSectionHeader(title = "Tracking")
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ModernActionCard(
                                        title = "Track Pakketten",
                                        description = "Volg live je pakket",
                                        icon = Icons.Filled.LocationOn,
                                        onClick = { navController.navigate("trackPackages/$userId") },
                                        containerColor = GreenSustainable
                                    )
                                    if (userRole != null && (userRole == "courier" || userRole == "admin")) {
                                        ModernActionCard(
                                            title = "Track Leveringen",
                                            description = "Volg live je leveringen",
                                            icon = Icons.Filled.LocalShipping,
                                            onClick = { navController.navigate("trackingDeliveries/$userId") },
                                            containerColor = DarkGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}