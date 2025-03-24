package com.example.quickdropapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.composables.forms.CourierRegistrationForm
import com.example.quickdropapp.models.Courier
import com.example.quickdropapp.models.CourierRequest
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun BecomeCourierScreen(navController: NavController, userId: Int) {
    var itsmeCode by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val apiService = RetrofitClient.instance

    Scaffold(containerColor = SandBeige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SandBeige)
        ) {
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
                    text = "Word Koerier",
                    color = GreenSustainable,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registreer je als koerier met verificatie",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                CourierRegistrationForm(
                    itsmeCode = itsmeCode,
                    onItsmeCodeChange = { itsmeCode = it },
                    licenseNumber = licenseNumber,
                    onLicenseNumberChange = { licenseNumber = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                successMessage?.let {
                    Text(
                        text = it,
                        color = GreenSustainable,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (itsmeCode.isBlank()) {
                            errorMessage = "Itsme verificatiecode is verplicht"
                            return@Button
                        }

                        val courierRequest = CourierRequest(
                            user_id = userId,
                            itsme_code = itsmeCode,
                            license_number = if (licenseNumber.isBlank()) null else licenseNumber
                        )

                        println("Sending courier request: $courierRequest")

                        val call = apiService.becomeCourier(courierRequest)
                        call.enqueue(object : Callback<Courier> {
                            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                                if (response.isSuccessful) {
                                    successMessage = "Je bent nu een koerier!"
                                    errorMessage = null
                                    navController.popBackStack()
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Geen details"
                                    errorMessage = "Fout bij registratie: ${response.code()} - $errorBody"
                                    successMessage = null
                                    println("Error response: ${response.code()} - $errorBody")
                                }
                            }

                            override fun onFailure(call: Call<Courier>, t: Throwable) {
                                errorMessage = "Netwerkfout: ${t.message}"
                                successMessage = null
                                println("Network failure: ${t.message}")
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSustainable),
                    shape = RoundedCornerShape(16.dp),
                    enabled = itsmeCode.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.BikeScooter,
                            contentDescription = "Registreer",
                            tint = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Registreer als Koerier",
                            color = SandBeige,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}