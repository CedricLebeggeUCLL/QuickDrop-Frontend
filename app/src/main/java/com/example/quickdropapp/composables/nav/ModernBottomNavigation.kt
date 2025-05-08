package com.example.quickdropapp.composables.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun ModernBottomNavigation(navController: NavController, userId: Int) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SandBeige)
            .padding(8.dp),
        containerColor = SandBeige,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("home/$userId", Icons.Filled.Home, "Home"),
            Triple("activities/$userId", Icons.Filled.FormatListNumbered, "Activities"), // Updated route
            Triple("profile/$userId", Icons.Filled.AccountCircle, "Profile")
        )
        items.forEach { (route, icon, label) ->
            val isSelected = navController.currentDestination?.route?.startsWith(route.split("/")[0]) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Prevent navigating to the same destination
                    if (!isSelected) {
                        navController.navigate(route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) GreenSustainable else DarkGreen.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) GreenSustainable else DarkGreen.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            )
        }
    }
}