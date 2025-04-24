package com.example.quickdropapp.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quickdropapp.data.AuthDataStore
import com.example.quickdropapp.models.auth.LoginRequest
import com.example.quickdropapp.models.auth.LoginResponse
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.network.TokenRefreshException
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Gebruik createPublic() voor inloggen, zodat de authInterceptor wordt overgeslagen
    val apiService = RetrofitClient.createPublic()

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
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Gebruikersnaam of E-mail", color = GreenSustainable) },
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
                .padding(bottom = 8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Wachtwoord verbergen" else "Wachtwoord tonen",
                        tint = GreenSustainable
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen
            )
        )

        TextButton(
            onClick = { navController.navigate("passwordRecovery") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Wachtwoord vergeten?",
                color = DarkGreen,
                style = MaterialTheme.typography.bodyMedium
            )
        }

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
                if (identifier.isNotEmpty() && password.isNotEmpty()) {
                    val loginRequest = LoginRequest(identifier, password)
                    val call = apiService.loginUser(loginRequest)
                    println("LoginScreen: Sending login request with identifier=$identifier")
                    call.enqueue(object : retrofit2.Callback<LoginResponse> {
                        override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                            println("LoginScreen: Response received, code=${response.code()}")
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                loginResponse?.let {
                                    println("LoginScreen: Successful login - userId=${it.userId}, accessToken=${it.accessToken}, refreshToken=${it.refreshToken}")
                                    scope.launch(Dispatchers.IO) {
                                        AuthDataStore.saveAuthData(context, it.userId, it.accessToken, it.refreshToken)
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
                                    401 -> "Ongeldige gebruikersnaam/e-mail of wachtwoord"
                                    500 -> "Serverfout, probeer het later opnieuw"
                                    else -> "Inloggen mislukt: ${response.message()}"
                                }
                                println("LoginScreen: Error - $errorMessage")
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                            errorMessage = if (t is TokenRefreshException) {
                                "Ongeldige gebruikersnaam/e-mail of wachtwoord"
                            } else {
                                "Netwerkfout: ${t.message}"
                            }
                            println("LoginScreen: Network failure - ${t.message}")
                        }
                    })
                } else {
                    errorMessage = "Vul gebruikersnaam/e-mail en wachtwoord in"
                    println("LoginScreen: Error - Identifier or password empty")
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