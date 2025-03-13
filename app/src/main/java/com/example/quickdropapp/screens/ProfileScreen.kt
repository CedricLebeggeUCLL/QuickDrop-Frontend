package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
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
import com.example.quickdropapp.composables.FlyoutMenu
import com.example.quickdropapp.composables.ModernBottomNavigation
import com.example.quickdropapp.models.User
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
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

    val apiService = RetrofitClient.instance

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
                    println("API fout: ${response.message()}")
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
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLogout = onLogout
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        ProfileContent(user, userId, navController)
                    }
                }
            }
        }
    )
}

@Composable
fun EnhancedHeaderProfile(onMenuClick: () -> Unit, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GreenSustainable.copy(alpha = 0.2f), SandBeige)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = GreenSustainable
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Profiel",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkGreen
                )
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(2.dp, CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Uitloggen",
                    tint = GreenSustainable
                )
            }
        }
    }
}

@Composable
fun ProfileContent(user: User?, userId: Int, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileInfoCard(user)
        SettingsCard(userId, navController)
        HistoryCard(userId, navController)
        HelpSupportCard(userId, navController)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileInfoCard(user: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Gebruiker",
                tint = GreenSustainable,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Gebruikersinformatie",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                if (user == null) {
                    Text(
                        text = "Gegevens niet beschikbaar",
                        fontSize = 16.sp,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "Naam: ${user.username}",
                        fontSize = 16.sp,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "E-mail: ${user.email}",
                        fontSize = 16.sp,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCard(userId: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp)
            .clickable { navController.navigate("settings/$userId") },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Instellingen",
                tint = DarkGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Instellingen",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Pas je voorkeuren aan",
                    fontSize = 16.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun HistoryCard(userId: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp)
            .clickable { navController.navigate("history/$userId") },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Geschiedenis",
                tint = GreenSustainable,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Geschiedenis",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Bekijk je activiteiten geschiedenis",
                    fontSize = 16.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun HelpSupportCard(userId: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp)
            .clickable { navController.navigate("helpSupport/$userId") },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = "Help & Support",
                tint = DarkGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Help & Support",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = "Krijg hulp bij je vragen",
                    fontSize = 16.sp,
                    color = DarkGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}