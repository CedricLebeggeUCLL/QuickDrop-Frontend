package com.example.quickdropapp.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    composable(
                        route = "home/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        HomeScreen(
                            navController = navController,
                            userId = userId,
                            onLogout = {
                                navController.popBackStack("welcome", inclusive = false)
                                navController.navigate("welcome")
                            }
                        )
                    }
                    composable(
                        route = "sendPackage/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        SendPackageScreen(navController, userId)
                    }
                    composable(
                        route = "becomeCourier/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        BecomeCourierScreen(navController, userId)
                    }
                    composable(
                        route = "startDelivery/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        StartDeliveryScreen(navController, userId)
                    }
                    composable(
                        route = "searchPackages/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        SearchPackagesScreen(navController, userId)
                    }
                    composable("trackDelivery") { TrackDeliveryScreen(navController) }
                    composable(
                        route = "viewDeliveries/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        ViewDeliveriesScreen(navController, userId)
                    }
                }
            }
        }
    }
}