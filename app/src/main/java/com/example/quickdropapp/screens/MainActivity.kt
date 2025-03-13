// MainActivity.kt
package com.example.quickdropapp.screens

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.screens.activities.deliveries.DeliveryInfoScreen
import com.example.quickdropapp.screens.activities.deliveries.StartDeliveryScreen
import com.example.quickdropapp.screens.activities.deliveries.ViewDeliveriesScreen
import com.example.quickdropapp.screens.activities.packages.SearchPackagesScreen
import com.example.quickdropapp.screens.activities.packages.SendPackageScreen
import com.example.quickdropapp.screens.activities.packages.UpdatePackageScreen
import com.example.quickdropapp.screens.activities.packages.ViewPackagesScreen
import com.example.quickdropapp.screens.activities.tracking.TrackDeliveryScreen
import com.example.quickdropapp.screens.auth.LoginScreen
import com.example.quickdropapp.screens.auth.RegisterScreen
import com.example.quickdropapp.screens.auth.WelcomeScreen
import com.example.quickdropapp.screens.main.ActivitiesOverviewScreen
import com.example.quickdropapp.screens.main.HomeScreen
import com.example.quickdropapp.screens.main.ProfileScreen
import com.example.quickdropapp.screens.profile.BecomeCourierScreen
import com.example.quickdropapp.screens.profile.HelpSupportScreen
import com.example.quickdropapp.screens.profile.HistoryScreen
import com.example.quickdropapp.screens.profile.SettingsScreen
import com.example.quickdropapp.ui.theme.QuickDropAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickDropAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                // State voor inlogstatus en userId
                var isLoggedIn by remember { mutableStateOf(false) }
                var initialUserId by remember { mutableStateOf(-1) }

                // Controleer inlogstatus bij opstarten met LaunchedEffect
                LaunchedEffect(Unit) {
                    isLoggedIn = AuthDataStore.isLoggedIn(context)
                    initialUserId = AuthDataStore.getUserId(context) ?: -1
                    if (isLoggedIn && initialUserId != -1) {
                        navController.navigate("home/$initialUserId") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {
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
                                scope.launch {
                                    AuthDataStore.clearAuthData(context)
                                }
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
                    composable(
                        route = "deliveryInfo/{deliveryId}",
                        arguments = listOf(navArgument("deliveryId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val deliveryId = backStackEntry.arguments?.getInt("deliveryId") ?: 0
                        DeliveryInfoScreen(navController, deliveryId)
                    }
                    composable(
                        route = "activitiesOverview/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        ActivitiesOverviewScreen(
                            navController = navController,
                            userId = userId,
                            onLogout = {
                                scope.launch {
                                    AuthDataStore.clearAuthData(context)
                                }
                                navController.popBackStack("welcome", inclusive = false)
                                navController.navigate("welcome")
                            }
                        )
                    }
                    composable(
                        route = "profile/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        ProfileScreen(
                            navController = navController,
                            userId = userId,
                            onLogout = {
                                scope.launch {
                                    AuthDataStore.clearAuthData(context)
                                }
                                navController.popBackStack("welcome", inclusive = false)
                                navController.navigate("welcome")
                            }
                        )
                    }
                    // Nieuwe routes toegevoegd
                    composable(
                        route = "viewPackages/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        ViewPackagesScreen(navController, userId)
                    }
                    composable(
                        route = "updatePackage/{packageId}",
                        arguments = listOf(navArgument("packageId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val packageId = backStackEntry.arguments?.getInt("packageId") ?: 0
                        UpdatePackageScreen(navController, packageId)
                    }
                    composable(
                        route = "trackPackage/{packageId}",
                        arguments = listOf(navArgument("packageId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val packageId = backStackEntry.arguments?.getInt("packageId") ?: 0
                        //TrackPackageScreen(navController, packageId) // Placeholder, implementeer later
                    }
                    composable(
                        route = "activeActivities/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        //ActiveActivitiesScreen(navController, userId) // Placeholder, implementeer later
                    }
                    composable(
                        route = "settings/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        SettingsScreen(navController, userId)
                    }
                    composable(
                        route = "history/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        HistoryScreen(navController, userId)
                    }
                    composable(
                        route = "helpSupport/{userId}",
                        arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        HelpSupportScreen(navController, userId)
                    }
                }
            }
        }
    }
}