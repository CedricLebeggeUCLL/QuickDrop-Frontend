package com.example.quickdropapp.screens.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quickdropapp.composables.forms.CourierRegistrationForm
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.example.quickdropapp.models.CourierRegistrationRequest
import kotlinx.serialization.json.Json
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeCourierScreen(
    navController: NavHostController,
    userId: Int
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("België") }
    var phoneCountry by remember { mutableStateOf("België") }
    var nationalNumber by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("België") }
    var termsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var itsmeVerified by remember { mutableStateOf(false) }

    // Mock itsme configuration
    val itsmeClientId = "mock_client_id_123"
    val itsmeRedirectUri = "com.example.quickdropapp://oauth/callback"
    val itsmeAuthUrl = "http://localhost:3000/itsme/mock/authorize" +
            "?response_type=code" +
            "&client_id=$itsmeClientId" +
            "&redirect_uri=$itsmeRedirectUri" +
            "&scope=openid%20profile" +
            "&state=quickdrop_mock_state"

    // Ktor client
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // itsme OAuth launcher
    val itsmeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (data != null) {
            val uri = data.data
            if (uri != null && uri.toString().startsWith(itsmeRedirectUri)) {
                val code = uri.getQueryParameter("code")
                val state = uri.getQueryParameter("state")
                Log.d("itsme", "Received URI: $uri, Code: $code, State: $state")
                if (code != null) {
                    MainScope().launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val response: HttpResponse = client.post("http://localhost:3000/api/couriers/itsme-callback") {
                                contentType(ContentType.Application.Json)
                                setBody(mapOf("code" to code, "user_id" to userId))
                            }
                            if (response.status == HttpStatusCode.OK) {
                                successMessage = "Mock itsme verification successful!"
                                itsmeVerified = true
                            } else {
                                errorMessage = response.bodyAsText()
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error during itsme verification: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    errorMessage = "Failed to retrieve itsme authorization code"
                }
            }
        }
    }

    // Validation functions
    fun isValidPhoneNumber(number: String): Boolean {
        return number.matches(Regex("^\\+\\d{10,15}\$"))
    }

    fun isValidBirthDate(date: String): Boolean {
        return date.matches(Regex("^\\d{2}/\\d{2}/\\d{4}\$")) &&
                try {
                    val parts = date.split("/")
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val year = parts[2].toInt()
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR) // Use Calendar for compatibility
                    day in 1..31 && month in 1..12 && year in 1900..currentYear
                } catch (e: Exception) {
                    false
                }
    }

    fun isValidNationalNumber(number: String): Boolean {
        return number.matches(Regex("^\\d{11}\$"))
    }

    // Check if all fields are filled and valid
    val allFieldsFilled = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            isValidBirthDate(birthDate) &&
            isValidPhoneNumber(mobileNumber) &&
            address.isNotBlank() &&
            city.isNotBlank() &&
            postalCode.isNotBlank() &&
            country.isNotBlank() &&
            phoneCountry.isNotBlank() &&
            isValidNationalNumber(nationalNumber) &&
            nationality.isNotBlank() &&
            termsAccepted

    // Submit courier registration
    suspend fun submitCourierRegistration() {
        isLoading = true
        errorMessage = null
        successMessage = null

        // Format phone number (remove any spaces)
        val formattedPhoneNumber = mobileNumber.replace("\\s+".toRegex(), "")

        val request = CourierRegistrationRequest(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            phoneNumber = formattedPhoneNumber,
            address = address,
            city = city,
            postalCode = postalCode,
            country = country,
            nationalNumber = nationalNumber,
            nationality = nationality
        )

        try {
            val response: HttpResponse = client.post("http://localhost:3000/api/couriers/become") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.Created) {
                successMessage = "Successfully registered as a courier!"
                navController.popBackStack()
            } else {
                errorMessage = "Registration failed: ${response.bodyAsText()}"
                Log.e("BecomeCourier", "Error response: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            Log.e("BecomeCourier", "Exception: ${e.stackTraceToString()}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = SandBeige,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SandBeige)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
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
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Display error or success messages
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                successMessage?.let {
                    Text(
                        text = it,
                        color = GreenSustainable,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Courier registration form
                CourierRegistrationForm(
                    onItsmeClick = {
                        Log.d("itsme", "Starting mock itsme OAuth flow")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itsmeAuthUrl))
                        itsmeLauncher.launch(intent)
                    },
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    lastName = lastName,
                    onLastNameChange = { lastName = it },
                    birthDate = birthDate,
                    onBirthDateChange = { birthDate = it },
                    mobileNumber = mobileNumber,
                    onMobileNumberChange = { mobileNumber = it },
                    address = address,
                    onAddressChange = { address = it },
                    city = city,
                    onCityChange = { city = it },
                    postalCode = postalCode,
                    onPostalCodeChange = { postalCode = it },
                    country = country,
                    onCountryChange = { country = it },
                    phoneCountry = phoneCountry,
                    onPhoneCountryChange = { phoneCountry = it },
                    nationalNumber = nationalNumber,
                    onNationalNumberChange = { nationalNumber = it },
                    nationality = nationality,
                    onNationalityChange = { nationality = it },
                    termsAccepted = termsAccepted,
                    onTermsAcceptedChange = { termsAccepted = it },
                    itsmeVerified = itsmeVerified
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Word Koerier" button
                Button(
                    onClick = {
                        if (allFieldsFilled) {
                            MainScope().launch {
                                submitCourierRegistration()
                            }
                        } else {
                            errorMessage = "Vul alle velden correct in."
                        }
                    },
                    enabled = allFieldsFilled && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (allFieldsFilled && !isLoading) 1f else 0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenSustainable,
                        disabledContainerColor = GreenSustainable.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = SandBeige,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Word Koerier",
                            color = SandBeige,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}