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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quickdropapp.composables.forms.Country
import com.example.quickdropapp.composables.forms.CourierRegistrationForm
import com.example.quickdropapp.models.courier.Courier
import com.example.quickdropapp.models.courier.CourierRegistrationRequest
import com.example.quickdropapp.network.ApiService
import com.example.quickdropapp.network.ItsmeCallbackResponse
import com.example.quickdropapp.network.RetrofitClient
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    var mobileNumber by remember { mutableStateOf("") } // Stores only the digits after the prefix
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

    // Get the ApiService instance
    val context = LocalContext.current
    val apiService = remember { RetrofitClient.create(context) }
    val scope = MainScope()

    // Mock itsme configuration
    val itsmeClientId = "mock_client_id_123"
    val itsmeRedirectUri = "com.example.quickdropapp://oauth/callback"
    val itsmeAuthUrl = "http://192.168.4.75:3000/itsme/mock/authorize" +
            "?response_type=code" +
            "&client_id=$itsmeClientId" +
            "&redirect_uri=$itsmeRedirectUri" +
            "&scope=openid%20profile" +
            "&state=quickdrop_mock_state"

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
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        apiService.itsmeCallback(mapOf("code" to code, "user_id" to userId)).enqueue(object : Callback<ItsmeCallbackResponse> {
                            override fun onResponse(call: Call<ItsmeCallbackResponse>, response: Response<ItsmeCallbackResponse>) {
                                if (response.isSuccessful) {
                                    successMessage = response.body()?.message ?: "Mock itsme verification successful!"
                                    itsmeVerified = true
                                } else {
                                    errorMessage = "itsme callback failed: ${response.errorBody()?.string()}"
                                    Log.e("BecomeCourier", "itsme callback failed: ${response.errorBody()?.string()}")
                                }
                                isLoading = false
                            }

                            override fun onFailure(call: Call<ItsmeCallbackResponse>, t: Throwable) {
                                errorMessage = "Error during itsme verification: ${t.message}"
                                Log.e("BecomeCourier", "itsme callback exception: ${t.message}")
                                isLoading = false
                            }
                        })
                    }
                } else {
                    errorMessage = "Failed to retrieve itsme authorization code"
                }
            }
        }
    }

    // Validation functions
    fun isValidPhoneNumber(digits: String, prefix: String): Boolean {
        val fullNumber = "$prefix$digits"
        return if (fullNumber.length >= 10) { // At least 10 characters, including country code
            fullNumber.matches(Regex("^\\+\\d{9,15}$"))
        } else {
            false // Require at least 10 characters to be valid
        }
    }

    fun isValidBirthDate(date: String): Boolean {
        return if (date.length == 10) { // Only validate when full length
            date.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$")) &&
                    try {
                        val parts = date.split("/")
                        val day = parts[0].toInt()
                        val month = parts[1].toInt()
                        val year = parts[2].toInt()
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        day in 1..31 && month in 1..12 && year in 1900..currentYear
                    } catch (e: Exception) {
                        false
                    }
        } else {
            false // Require exactly 10 characters to be valid
        }
    }

    fun isValidNationalNumber(number: String): Boolean {
        return if (number.length == 11) {
            number.matches(Regex("^\\d{11}$"))
        } else {
            false // Require exactly 11 digits to be valid
        }
    }

    // Compute the full phone number for validation and submission
    val selectedPhoneCountry = COUNTRIES.find { it.name == phoneCountry } ?: COUNTRIES[0]
    val fullPhoneNumber = "${selectedPhoneCountry.phonePrefix}$mobileNumber"

    // Check if all fields are filled and valid
    val allFieldsFilled = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            birthDate.length == 10 && isValidBirthDate(birthDate) &&
            mobileNumber.length >= 9 && isValidPhoneNumber(mobileNumber, selectedPhoneCountry.phonePrefix) &&
            address.isNotBlank() &&
            city.isNotBlank() &&
            postalCode.isNotBlank() &&
            country.isNotBlank() &&
            phoneCountry.isNotBlank() &&
            nationalNumber.length == 11 && isValidNationalNumber(nationalNumber) &&
            nationality.isNotBlank() &&
            termsAccepted

    // Debug logging to identify which condition fails
    LaunchedEffect(firstName, lastName, birthDate, mobileNumber, address, city, postalCode, country, phoneCountry, nationalNumber, nationality, termsAccepted) {
        Log.d("BecomeCourierScreen", "allFieldsFilled: $allFieldsFilled")
        Log.d("BecomeCourierScreen", "firstName: $firstName, isNotBlank: ${firstName.isNotBlank()}")
        Log.d("BecomeCourierScreen", "lastName: $lastName, isNotBlank: ${lastName.isNotBlank()}")
        Log.d("BecomeCourierScreen", "birthDate: $birthDate, length: ${birthDate.length}, isValid: ${isValidBirthDate(birthDate)}")
        Log.d("BecomeCourierScreen", "mobileNumber: $mobileNumber, fullPhoneNumber: $fullPhoneNumber, length: ${mobileNumber.length}, isValid: ${isValidPhoneNumber(mobileNumber, selectedPhoneCountry.phonePrefix)}")
        Log.d("BecomeCourierScreen", "address: $address, isNotBlank: ${address.isNotBlank()}")
        Log.d("BecomeCourierScreen", "city: $city, isNotBlank: ${city.isNotBlank()}")
        Log.d("BecomeCourierScreen", "postalCode: $postalCode, isNotBlank: ${postalCode.isNotBlank()}")
        Log.d("BecomeCourierScreen", "country: $country, isNotBlank: ${country.isNotBlank()}")
        Log.d("BecomeCourierScreen", "phoneCountry: $phoneCountry, isNotBlank: ${phoneCountry.isNotBlank()}")
        Log.d("BecomeCourierScreen", "nationalNumber: $nationalNumber, length: ${nationalNumber.length}, isValid: ${isValidNationalNumber(nationalNumber)}")
        Log.d("BecomeCourierScreen", "nationality: $nationality, isNotBlank: ${nationality.isNotBlank()}")
        Log.d("BecomeCourierScreen", "termsAccepted: $termsAccepted")
    }

    // Submit courier registration using ApiService
    fun submitCourierRegistration() {
        isLoading = true
        errorMessage = null
        successMessage = null

        // Use the full phone number for submission
        val formattedPhoneNumber = fullPhoneNumber.replace("\\s+".toRegex(), "")

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

        // Log the request data for debugging
        Log.d("BecomeCourierScreen", "Submitting request: $request")

        apiService.becomeCourier(request).enqueue(object : Callback<Courier> {
            override fun onResponse(call: Call<Courier>, response: Response<Courier>) {
                if (response.isSuccessful) {
                    successMessage = "Successfully registered as a courier!"
                    navController.popBackStack()
                } else {
                    errorMessage = "Registration failed: ${response.errorBody()?.string()}"
                    Log.e("BecomeCourier", "Error response: ${response.errorBody()?.string()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Courier>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
                Log.e("BecomeCourier", "Exception: ${t.message}")
                isLoading = false
            }
        })
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

                // "Word Koerier" button - Only clickable when form is fully filled
                Button(
                    onClick = {
                        scope.launch {
                            submitCourierRegistration()
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

// Define the COUNTRIES list here as well, since it's used in BecomeCourierScreen.kt
private val COUNTRIES = listOf(
    Country("België", "\uD83C\uDDE7\uD83C\uDDEA", "+32"),
    Country("Nederland", "\uD83C\uDDF3\uD83C\uDDF1", "+31"),
    Country("Frankrijk", "\uD83C\uDDEB\uD83C\uDDF7", "+33")
)