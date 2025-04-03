package com.example.quickdropapp.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.nav.FlyoutMenu
import com.example.quickdropapp.composables.nav.ModernBottomNavigation
import com.example.quickdropapp.composables.profile.*
import com.example.quickdropapp.models.auth.User
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController, userId: Int, onLogout: () -> Unit) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val apiService = RetrofitClient.create(LocalContext.current)

    // Fetch user data using userId
    LaunchedEffect(userId) {
        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                isLoading = false
                if (response.isSuccessful) {
                    user = response.body()
                    if (user == null) {
                        println("Geen gebruiker gevonden voor userId: $userId")
                    }
                } else {
                    println("API fout: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                isLoading = false
                println("Netwerkfout: ${t.message}")
            }
        })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FlyoutMenu(
                navController = navController,
                userId = userId,
                userRole = user?.role,
                onClose = { scope.launch { drawerState.close() } },
                onLogout = onLogout
            )
        },
        content = {
            Scaffold(
                bottomBar = { ModernBottomNavigation(navController, userId) },
                containerColor = SandBeige
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EnhancedHeaderProfile(
                        user = user,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLogout = onLogout
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        ProfileContent(user = user, userId = userId, navController = navController)
                    }
                }
            }
        }
    )
}