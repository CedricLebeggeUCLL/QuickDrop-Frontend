package com.example.quickdropapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.auth.User
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SettingsScreen(navController: NavController, userId: Int) {
    var user by remember { mutableStateOf<User?>(null) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUsernameError by remember { mutableStateOf(false) }
    val apiService = RetrofitClient.instance

    LaunchedEffect(userId) {
        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()
                    username = user?.username ?: ""
                    email = user?.email ?: ""
                } else {
                    message = "Fout bij het laden van gegevens"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                message = "Netwerkfout: ${t.message}"
                isLoading = false
            }
        })
    }

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
            // Uniforme header zonder witte gradiënt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Terug",
                        tint = GreenSustainable,
                        modifier = Modifier
                            .size(32.dp)
                            .background(SandBeige.copy(alpha = 0.2f), CircleShape)
                            .padding(6.dp)
                    )
                }
                Text(
                    text = "Instellingen",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Beheer je profiel",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = GreenSustainable,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            isUsernameError = it.isEmpty()
                        },
                        label = { Text("Gebruikersnaam") },
                        isError = isUsernameError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = GreenSustainable,
                            unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.5f),
                            errorIndicatorColor = Color.Red
                        )
                    )
                    if (isUsernameError) {
                        Text(
                            "Gebruikersnaam is verplicht",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = GreenSustainable,
                            unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (!isUsernameError) {
                                val updatedUser = User(userId, username, email, user?.password ?: "", user?.role ?: "user")
                                apiService.updateUser(userId, updatedUser).enqueue(object : Callback<User> {
                                    override fun onResponse(call: Call<User>, response: Response<User>) {
                                        message = if (response.isSuccessful) "Gegevens opgeslagen!" else "Fout bij opslaan"
                                    }

                                    override fun onFailure(call: Call<User>, t: Throwable) {
                                        message = "Fout bij opslaan: ${t.message}"
                                    }
                                })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable)
                    ) {
                        Text("Opslaan", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                    message?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            it,
                            color = if (it.contains("Fout")) Color.Red else DarkGreen,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}