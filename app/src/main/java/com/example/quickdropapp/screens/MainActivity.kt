package com.example.quickdropapp.screens

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
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
import com.example.quickdropapp.screens.activities.tracking.TrackPackagesScreen
import com.example.quickdropapp.screens.activities.tracking.TrackingDeliveriesScreen
import com.example.quickdropapp.screens.auth.LoginScreen
import com.example.quickdropapp.screens.auth.PasswordRecoveryScreen
import com.example.quickdropapp.screens.auth.RegisterScreen
import com.example.quickdropapp.screens.auth.ResetPasswordScreen
import com.example.quickdropapp.screens.auth.WelcomeScreen
import com.example.quickdropapp.screens.main.ActivitiesOverviewScreen
import com.example.quickdropapp.screens.main.HomeScreen
import com.example.quickdropapp.screens.main.ProfileScreen
import com.example.quickdropapp.screens.profile.BecomeCourierScreen
import com.example.quickdropapp.screens.profile.HelpSupportScreen
import com.example.quickdropapp.screens.profile.HistoryScreen
import com.example.quickdropapp.screens.profile.SettingsScreen
import com.example.quickdropapp.ui.theme.QuickDropAppTheme
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Places.initialize(applicationContext, "AIzaSyBhUNOle29taWD_B58yNpmsUDBihvkqq98")

        var deepLinkToken: String? = null
        val uri = intent.data
        if (uri != null && uri.scheme == "quickdrop" && uri.host == "resetPassword") {
            deepLinkToken = uri.pathSegments.firstOrNull()
            Log.d("DeepLink", "Token ontvangen: $deepLinkToken")
        }

        setContent {
            QuickDropAppTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                val isLoggedIn = AuthDataStore.isLoggedIn(this)
                val initialUserId = if (isLoggedIn) AuthDataStore.getUserId(this) ?: -1 else -1

                LaunchedEffect(Unit) {
                    if (isLoggedIn && initialUserId != -1) {
                        navController.navigate("home/$initialUserId") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else if (deepLinkToken != null) {
                        Log.d("DeepLink", "Navigeren naar resetPassword met token: $deepLinkToken")
                        navController.navigate("resetPassword/$deepLinkToken")
                    }
                }

                androidx.compose.material3.Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "welcome",
                        modifier = androidx.compose.ui.Modifier.padding(paddingValues)
                    ) {
                        composable("welcome") { WelcomeScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("passwordRecovery") { PasswordRecoveryScreen(navController) }
                        composable(
                            route = "resetPassword/{token}",
                            arguments = listOf(navArgument("token") { type = androidx.navigation.NavType.StringType })
                        ) { backStackEntry ->
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            ResetPasswordScreen(navController, token)
                        }
                        composable(
                            route = "home/{userId}",
                            arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            HomeScreen(
                                navController = navController,
                                userId = userId,
                                onLogout = {
                                    coroutineScope.launch {
                                        AuthDataStore.clearAuthData(this@MainActivity)
                                        navController.navigate("welcome") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        snackbarHostState.showSnackbar("Uitgelogd")
                                    }
                                }
                            )
                        }
                        composable(
                            route = "sendPackage/{userId}/{actionType}/{category}",
                            arguments = listOf(
                                navArgument("userId") { type = androidx.navigation.NavType.IntType },
                                navArgument("actionType") { type = androidx.navigation.NavType.StringType },
                                navArgument("category") { type = androidx.navigation.NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val actionType = backStackEntry.arguments?.getString("actionType") ?: "send"
                            val category = backStackEntry.arguments?.getString("category") ?: "package"
                            SendPackageScreen(navController, userId, actionType, category)
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
                        composable(
                            route = "trackPackages/{userId}",
                            arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            TrackPackagesScreen(navController = navController, userId = userId)
                        }
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
                                    coroutineScope.launch {
                                        AuthDataStore.clearAuthData(this@MainActivity)
                                        navController.navigate("welcome") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        snackbarHostState.showSnackbar("Uitgelogd")
                                    }
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
                                    coroutineScope.launch {
                                        AuthDataStore.clearAuthData(this@MainActivity)
                                        navController.navigate("welcome") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        snackbarHostState.showSnackbar("Uitgelogd")
                                    }
                                }
                            )
                        }
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
                        composable(
                            route = "trackingDeliveries/{userId}",
                            arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            TrackingDeliveriesScreen(navController = navController, userId = userId)
                        }
                    }
                }
            }
        }
    }
}