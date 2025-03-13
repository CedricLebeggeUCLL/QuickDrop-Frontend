package com.example.quickdropapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun FlyoutMenu(navController: NavController, userId: Int, userRole: String?, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(SandBeige)
            .padding(16.dp),
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

        HorizontalDivider(thickness = 1.dp, color = GreenSustainable.copy(alpha = 0.2f))

        // Pakketten sectie
        NavigationDrawerItem(
            label = { Text("Pakket Versturen", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("sendPackage/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.DoubleArrow, contentDescription = null, tint = GreenSustainable) }
        )
        NavigationDrawerItem(
            label = { Text("Mijn Pakketten", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("viewPackages/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = GreenSustainable) }
        )
        if (userRole == "user") { // Alleen voor niet-couriers
            NavigationDrawerItem(
                label = { Text("Word Koerier", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("becomeCourier/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.DriveEta, contentDescription = null, tint = GreenSustainable) }
            )
        }

        // Leveringen sectie (voor couriers en admins)
        if (userRole == "courier" || userRole == "admin") {
            HorizontalDivider(thickness = 1.dp, color = GreenSustainable.copy(alpha = 0.2f))
            NavigationDrawerItem(
                label = { Text("Start een Levering", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("startDelivery/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.DriveEta, contentDescription = null, tint = GreenSustainable) }
            )
            NavigationDrawerItem(
                label = { Text("Mijn Leveringen", color = DarkGreen) },
                selected = false,
                onClick = {
                    navController.navigate("viewDeliveries/$userId")
                    onClose()
                },
                icon = { Icon(Icons.Filled.FormatListNumbered, contentDescription = null, tint = GreenSustainable) }
            )
        }

        // Actieve activiteiten
        HorizontalDivider(thickness = 1.dp, color = GreenSustainable.copy(alpha = 0.2f))
        NavigationDrawerItem(
            label = { Text("Track Levering", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("trackDelivery")
                onClose()
            },
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GreenSustainable) }
        )
        NavigationDrawerItem(
            label = { Text("Actieve Activiteiten", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("activeActivities/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.LocalActivity, contentDescription = null, tint = GreenSustainable) }
        )

        // Profiel en uitloggen
        HorizontalDivider(thickness = 1.dp, color = GreenSustainable.copy(alpha = 0.2f))
        NavigationDrawerItem(
            label = { Text("Profiel", color = DarkGreen) },
            selected = false,
            onClick = {
                navController.navigate("profile/$userId")
                onClose()
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = null, tint = GreenSustainable) }
        )
    }
}