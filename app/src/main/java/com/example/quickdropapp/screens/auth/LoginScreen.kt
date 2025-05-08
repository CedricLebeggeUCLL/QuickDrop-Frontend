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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

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
                if (identifier.isBlank() || password.isBlank()) {
                    errorMessage = "Vul gebruikersnaam/e-mail en wachtwoord in"
                } else {
                    val loginRequest = LoginRequest(identifier, password)
                    val call = apiService.loginUser(loginRequest)
                    call.enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                loginResponse?.let {
                                    scope.launch(Dispatchers.IO) {
                                        AuthDataStore.saveAuthData(context, it.userId, it.accessToken, it.refreshToken)
                                        launch(Dispatchers.Main) {
                                            navController.navigate("home/${it.userId}") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        }
                                    }
                                } ?: run {
                                    errorMessage = "Geen geldige respons ontvangen"
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorJson = errorBody?.let { JSONObject(it) }
                                errorMessage = errorJson?.getString("error") ?: when (response.code()) {
                                    401 -> "Ongeldige gebruikersnaam/e-mail of wachtwoord"
                                    500 -> "Serverfout, probeer het later opnieuw"
                                    else -> "Inloggen mislukt: ${response.message()}"
                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            errorMessage = if (t is TokenRefreshException) {
                                "Ongeldige gebruikersnaam/e-mail of wachtwoord"
                            } else {
                                "Netwerkfout: ${t.message}"
                            }
                        }
                    })
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