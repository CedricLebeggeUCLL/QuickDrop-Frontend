package com.example.quickdropapp.screens.profile

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
    var phoneCountry by remember { mutableStateOf("België") } // Nieuwe state voor telefoonnummer land
    var documentType by remember { mutableStateOf("Belgische identiteitskaart") }
    var nationalNumber by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("België") }
    var termsAccepted by remember { mutableStateOf(false) }

    // Controleer of alle velden zijn ingevuld en de checkbox is aangevinkt
    val allFieldsFilled = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            birthDate.isNotBlank() &&
            mobileNumber.isNotBlank() &&
            address.isNotBlank() &&
            city.isNotBlank() &&
            postalCode.isNotBlank() &&
            country.isNotBlank() &&
            phoneCountry.isNotBlank() &&
            documentType.isNotBlank() &&
            nationalNumber.isNotBlank() &&
            nationality.isNotBlank() &&
            termsAccepted

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

                // Roep CourierRegistrationForm aan voor de inhoud
                CourierRegistrationForm(
                    onItsmeClick = {
                        // Handel itsme-verificatie af (backend-logica komt later)
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
                    phoneCountry = phoneCountry, // Nieuwe parameter
                    onPhoneCountryChange = { phoneCountry = it }, // Nieuwe callback
                    nationalNumber = nationalNumber,
                    onNationalNumberChange = { nationalNumber = it },
                    nationality = nationality,
                    onNationalityChange = { nationality = it },
                    termsAccepted = termsAccepted,
                    onTermsAcceptedChange = { termsAccepted = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Word Koerier"-knop
                Button(
                    onClick = {
                        // Actie wanneer de knop wordt geklikt (backend-logica komt later)
                        println("Word Koerier geklikt!")
                    },
                    enabled = allFieldsFilled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (allFieldsFilled) 1f else 0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenSustainable,
                        disabledContainerColor = GreenSustainable
                    )
                ) {
                    Text(
                        text = "Word Koerier",
                        color = SandBeige,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}