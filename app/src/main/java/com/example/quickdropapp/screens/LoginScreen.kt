// com.example.quickdropapp.screens/LoginScreen.kt
package com.example.quickdropapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.models.LoginRequest
import com.example.quickdropapp.models.LoginResponse
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                    println("LoginScreen: Sending login request with email=$email")
                    call.enqueue(object : Callback<LoginResponse> { // Wijzig naar LoginResponse
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            println("LoginScreen: Response received, code=${response.code()}")
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                loginResponse?.let {
                                    println("LoginScreen: Successful login - userId=${it.userId}, token=${it.token}")
                                    scope.launch(Dispatchers.IO) {
                                        AuthDataStore.saveAuthData(context, it.userId, it.token)
                                        launch(Dispatchers.Main) {
                                            println("LoginScreen: Navigating to home/${it.userId}")
                                            navController.navigate("home/${it.userId}") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        }
                                    }
                                } ?: run {
                                    errorMessage = "Geen geldige respons ontvangen"
                                    println("LoginScreen: Error - No valid response")
                                }
                            } else {
                                errorMessage = when (response.code()) {
                                    401 -> "Ongeldige e-mail of wachtwoord"
                                    500 -> "Serverfout, probeer het later opnieuw"
                                    else -> "Inloggen mislukt: ${response.message()}"
                                }
                                println("LoginScreen: Error - $errorMessage")
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            errorMessage = "Netwerkfout: ${t.message}"
                            println("LoginScreen: Network failure - ${t.message}")
                        }
                    })
                } else {
                    errorMessage = "Vul e-mail en wachtwoord in"
                    println("LoginScreen: Error - Email or password empty")
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