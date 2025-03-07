package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.models.LoginRequest
import com.example.quickdropapp.models.User
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val apiService = RetrofitClient.instance

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Inloggen",
            style = MaterialTheme.typography.displayMedium,
            color = GreenSustainable,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail", color = GreenSustainable) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Wachtwoord", color = GreenSustainable) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen
            )
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    val loginRequest = LoginRequest(email, password)
                    val call = apiService.loginUser(loginRequest)
                    call.enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                val user = response.body()
                                val userId = user?.id ?: 0
                                if (userId > 0) {
                                    navController.navigate("home/$userId")
                                } else {
                                    errorMessage = "Gebruikers-ID niet gevonden in de respons"
                                }
                            } else {
                                errorMessage = when (response.code()) {
                                    401 -> "Ongeldige e-mail of wachtwoord"
                                    500 -> "Serverfout, probeer het later opnieuw"
                                    else -> "Inloggen mislukt: ${response.message()}"
                                }
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            errorMessage = "Netwerkfout: ${t.message}"
                        }
                    })
                } else {
                    errorMessage = "Vul e-mail en wachtwoord in"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "Inloggen",
                style = MaterialTheme.typography.titleLarge,
                color = SandBeige
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Ben je nieuw? Registreer hier",
                color = DarkGreen,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}