package com.example.quickdropapp.composables.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.launch

@Composable
fun FlyoutMenu(
    navController: NavController,
    userId: Int,
    userRole: String?,
    onClose: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(SandBeige)
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header van de drawer
        Text(
            text = "Menu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GreenSustainable,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Pakketten sectie
        Text(
            text = "Pakketten",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        NavigationDrawerItem(
            label = { Text("Pakket Versturen", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("activities/$userId?openSheet=true")
                onClose()
            },
            icon = { Icon(Icons.Filled.DoubleArrow, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                unselectedTextColor = DarkGreen,
                unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
            )
        )
        NavigationDrawerItem(
            label = { Text("Mijn Pakketten", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("viewPackages/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                unselectedTextColor = DarkGreen,
                unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
            )
        )
        if (userRole == "user") {
            NavigationDrawerItem(
                label = { Text("Word Koerier", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("becomeCourier/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.DriveEta, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                    unselectedTextColor = DarkGreen,
                    unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
                )
            )
        }

        // Leveringen sectie (voor couriers en admins)
        if (userRole == "courier" || userRole == "admin") {
            Text(
                text = "Leveringen",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            NavigationDrawerItem(
                label = { Text("Start een Levering", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("startDelivery/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.DriveEta, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                    unselectedTextColor = DarkGreen,
                    unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
                )
            )
            NavigationDrawerItem(
                label = { Text("Mijn Leveringen", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("viewDeliveries/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.FormatListNumbered, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                    unselectedTextColor = DarkGreen,
                    unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
                )
            )
        }

        // Tracking sectie
        Text(
            text = "Tracking",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        NavigationDrawerItem(
            label = { Text("Track Pakketten", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("trackPackages/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                unselectedTextColor = DarkGreen,
                unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
            )
        )
        if (userRole == "courier" || userRole == "admin") {
            NavigationDrawerItem(
                label = { Text("Track Leveringen", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("trackingDeliveries/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                    unselectedTextColor = DarkGreen,
                    unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
                )
            )
        }

        // Profiel sectie
        Text(
            text = "Profiel",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        NavigationDrawerItem(
            label = { Text("Profiel", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("profile/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                unselectedTextColor = DarkGreen,
                unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
            )
        )

        // Uitloggen sectie
        Text(
            text = "Uitloggen",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        NavigationDrawerItem(
            label = { Text("Uitloggen", color = DarkGreen) },
            selected = false,
            onClick = {
                scope.launch {
                    AuthDataStore.clearAuthData(context)
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                onClose()
            },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = GreenSustainable.copy(alpha = 0.8f)) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = SandBeige.copy(alpha = 0.95f),
                unselectedTextColor = DarkGreen,
                unselectedIconColor = GreenSustainable.copy(alpha = 0.8f)
            )
        )
    }
}