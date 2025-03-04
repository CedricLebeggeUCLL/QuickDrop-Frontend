package com.example.quickdropapp.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdropapp.ui.theme.QuickDropAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickDropAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { WelcomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            onLogout = {
                                navController.popBackStack("welcome", inclusive = false)
                                navController.navigate("welcome")
                            }
                        )
                    }
                    composable("sendPackage") { SendPackageScreen(navController) }
                    composable("becomeCourier") { BecomeCourierScreen(navController) }
                    composable("trackDelivery") { TrackDeliveryScreen(navController) }
                }
            }
        }
    }
}