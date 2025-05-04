package com.example.quickdropapp.composables.forms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige
import java.util.Calendar

data class Country(val name: String, val flagEmoji: String, val phonePrefix: String)

private val COUNTRIES = listOf(
    Country("België", "\uD83C\uDDE7\uD83C\uDDEA", "+32"),
    Country("Nederland", "\uD83C\uDDF3\uD83C\uDDF1", "+31"),
    Country("Frankrijk", "\uD83C\uDDEB\uD83C\uDDF7", "+33")
)

private val NATIONALITIES = listOf("België", "Nederland", "Frankrijk")

class PrefixVisualTransformation(private val prefix: String) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val transformedText = prefix + text.text
        val offsetMapping = object : androidx.compose.ui.text.input.OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + prefix.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return maxOf(0, offset - prefix.length)
            }
        }
        return androidx.compose.ui.text.input.TransformedText(
            text = androidx.compose.ui.text.AnnotatedString(transformedText),
            offsetMapping = offsetMapping
        )
    }
}

// Validation functions (moved outside composable to avoid @Composable invocation issues)
private fun isValidBirthDate(date: String): Boolean {
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

private fun isValidPhoneNumber(digits: String): Boolean {
    return if (digits.length >= 9) { // At least 9 digits (after the prefix)
        digits.matches(Regex("^\\d{9,15}$"))
    } else {
        false // Require at least 9 digits to be valid
    }
}

private fun isValidNationalNumber(number: String): Boolean {
    return if (number.length == 11) {
        number.matches(Regex("^\\d{11}$"))
    } else {
        false // Require exactly 11 digits to be valid
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CourierRegistrationForm(
    onItsmeClick: () -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    mobileNumber: String, // Stores only the digits after the prefix
    onMobileNumberChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    postalCode: String,
    onPostalCodeChange: (String) -> Unit,
    country: String,
    onCountryChange: (String) -> Unit,
    phoneCountry: String,
    onPhoneCountryChange: (String) -> Unit,
    nationalNumber: String,
    onNationalNumberChange: (String) -> Unit,
    nationality: String,
    onNationalityChange: (String) -> Unit,
    termsAccepted: Boolean,
    onTermsAcceptedChange: (Boolean) -> Unit,
    itsmeVerified: Boolean = false
) {
    val selectedCountry = COUNTRIES.find { it.name == country } ?: COUNTRIES[0]
    val selectedPhoneCountry = COUNTRIES.find { it.name == phoneCountry } ?: COUNTRIES[0]

    // State to track focus for validation
    var birthDateFocused by remember { mutableStateOf(false) }
    var phoneFocused by remember { mutableStateOf(false) }

    // Apply validations
    val isBirthDateValid = isValidBirthDate(birthDate)
    val isPhoneValid = isValidPhoneNumber(mobileNumber)
    val isNationalNumberValid = isValidNationalNumber(nationalNumber)

    // Compute the full phone number for display and submission
    val fullPhoneNumber = "${selectedPhoneCountry.phonePrefix}$mobileNumber"

    // Debug logging for form values
    LaunchedEffect(firstName, lastName, birthDate, mobileNumber, address, city, postalCode, country, phoneCountry, nationalNumber, nationality, termsAccepted) {
        Log.d("CourierRegistrationForm", "firstName: $firstName")
        Log.d("CourierRegistrationForm", "lastName: $lastName")
        Log.d("CourierRegistrationForm", "birthDate: $birthDate, isValid: $isBirthDateValid")
        Log.d("CourierRegistrationForm", "mobileNumber: $mobileNumber, fullPhoneNumber: $fullPhoneNumber, isValid: $isPhoneValid")
        Log.d("CourierRegistrationForm", "address: $address")
        Log.d("CourierRegistrationForm", "city: $city")
        Log.d("CourierRegistrationForm", "postalCode: $postalCode")
        Log.d("CourierRegistrationForm", "country: $country")
        Log.d("CourierRegistrationForm", "phoneCountry: $phoneCountry")
        Log.d("CourierRegistrationForm", "nationalNumber: $nationalNumber, isValid: $isNationalNumberValid")
        Log.d("CourierRegistrationForm", "nationality: $nationality")
        Log.d("CourierRegistrationForm", "termsAccepted: $termsAccepted")
    }

    // Date picker setup
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Parse the current birthDate (if any) to set the initial date in the picker
    val initialYear = if (birthDate.isNotEmpty() && birthDate.length == 10) {
        try {
            birthDate.split("/")[2].toInt()
        } catch (e: Exception) {
            year - 18 // Default to 18 years ago if parsing fails
        }
    } else {
        year - 18 // Default to 18 years ago
    }
    val initialMonth = if (birthDate.isNotEmpty() && birthDate.length == 10) {
        try {
            birthDate.split("/")[1].toInt() - 1 // Month is 0-based in DatePickerDialog
        } catch (e: Exception) {
            month
        }
    } else {
        month
    }
    val initialDay = if (birthDate.isNotEmpty() && birthDate.length == 10) {
        try {
            birthDate.split("/")[0].toInt()
        } catch (e: Exception) {
            day
        }
    } else {
        day
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            onBirthDateChange(formattedDate)
        },
        initialYear,
        initialMonth,
        initialDay
    ).apply {
        datePicker.minDate = Calendar.getInstance().apply {
            set(1900, 0, 1)
        }.timeInMillis
        datePicker.maxDate = Calendar.getInstance().timeInMillis
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SandBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // itsme Verification Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Registreer je snel via itsme®",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                if (itsmeVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "itsme Verified",
                        tint = GreenSustainable,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Met itsme® registreer je snel en veilig. Je identiteit wordt automatisch geverifieerd, zodat je direct aan de slag kunt als koerier!",
                fontSize = 14.sp,
                color = DarkGreen.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onItsmeClick,
                enabled = !itsmeVerified,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6200),
                    disabledContainerColor = DarkGreen.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = if (itsmeVerified) "Geverifieerd via itsme®" else "Registreer je via itsme®",
                    color = SandBeige,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Divider with "OR"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = DarkGreen.copy(alpha = 0.3f)
                )
                Text(
                    text = "OF",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 14.sp,
                    color = DarkGreen
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = DarkGreen.copy(alpha = 0.3f)
                )
            }

            // Personal Information
            Text(
                text = "Persoonlijke Informatie",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = { Text("Voornaam") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Achternaam") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = birthDate,
                onValueChange = {}, // Read-only field
                label = { Text("Geboortedatum (dd/mm/yyyy)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> birthDateFocused = focusState.isFocused },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Selecteer geboortedatum"
                        )
                    }
                },
                isError = birthDate.isNotEmpty() && !birthDateFocused && !isBirthDateValid,
                supportingText = {
                    if (birthDate.isNotEmpty() && !birthDateFocused && !isBirthDateValid) {
                        Text(
                            text = "Gebruik formaat dd/mm/yyyy, dag 1-31, maand 1-12, jaar 1900-${Calendar.getInstance().get(Calendar.YEAR)}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                var expandedPhoneCountry by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(end = 8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedPhoneCountry.flagEmoji,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(top = 8.dp),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedPhoneCountry = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Selecteer telefoonnummer land"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenSustainable,
                            unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                            cursorColor = GreenSustainable,
                            focusedLabelColor = GreenSustainable
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = expandedPhoneCountry,
                        onDismissRequest = { expandedPhoneCountry = false }
                    ) {
                        COUNTRIES.forEach { countryOption ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = countryOption.flagEmoji,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(countryOption.name)
                                    }
                                },
                                onClick = {
                                    onPhoneCountryChange(countryOption.name)
                                    // Reset mobile number digits to empty
                                    onMobileNumberChange("")
                                    expandedPhoneCountry = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { newValue ->
                        // Only accept digits and update the mobile number (without prefix)
                        val digitsOnly = newValue.filter { it.isDigit() }.take(15)
                        onMobileNumberChange(digitsOnly)
                    },
                    label = { Text("Mobiele telefoon (e.g. ${selectedPhoneCountry.phonePrefix}123456789)") },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState -> phoneFocused = focusState.isFocused },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = PrefixVisualTransformation(selectedPhoneCountry.phonePrefix),
                    isError = mobileNumber.isNotEmpty() && !phoneFocused && !isPhoneValid,
                    supportingText = {
                        if (mobileNumber.isNotEmpty() && !phoneFocused && !isPhoneValid) {
                            Text(
                                text = "Gebruik formaat ${selectedPhoneCountry.phonePrefix}xxxxxxxxx (9-15 cijfers)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenSustainable,
                        unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                        cursorColor = GreenSustainable,
                        focusedLabelColor = GreenSustainable
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Address Section
            Text(
                text = "Adres (voor verificatie)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Straatnaam") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("Stad") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = postalCode,
                onValueChange = onPostalCodeChange,
                label = { Text("Postcode") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            var expandedCountry by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = "${selectedCountry.flagEmoji} ${selectedCountry.name}",
                    onValueChange = {},
                    label = { Text("Land") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedCountry = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Selecteer land"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenSustainable,
                        unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                        cursorColor = GreenSustainable,
                        focusedLabelColor = GreenSustainable
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = expandedCountry,
                    onDismissRequest = { expandedCountry = false }
                ) {
                    COUNTRIES.forEach { countryOption ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = countryOption.flagEmoji,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(countryOption.name)
                                }
                            },
                            onClick = {
                                onCountryChange(countryOption.name)
                                expandedCountry = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identity Information
            Text(
                text = "Identiteitsgegevens",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = "Belgische identiteitskaart",
                onValueChange = {},
                label = { Text("Documenttype") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = DarkGreen.copy(alpha = 0.6f),
                    disabledTextColor = DarkGreen.copy(alpha = 0.6f),
                    disabledLabelColor = DarkGreen.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = nationalNumber,
                onValueChange = { if (it.length <= 11) onNationalNumberChange(it) },
                label = { Text("Belgisch rijksregisternummer (11 cijfers)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = nationalNumber.isNotEmpty() && !isNationalNumberValid,
                supportingText = {
                    if (nationalNumber.isNotEmpty() && !isNationalNumberValid) {
                        Text(
                            text = "Moet 11 cijfers zijn",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            var expandedNationality by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = nationality,
                    onValueChange = {},
                    label = { Text("Nationaliteit") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedNationality = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Selecteer nationaliteit"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenSustainable,
                        unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                        cursorColor = GreenSustainable,
                        focusedLabelColor = GreenSustainable
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = expandedNationality,
                    onDismissRequest = { expandedNationality = false }
                ) {
                    NATIONALITIES.forEach { nat ->
                        DropdownMenuItem(
                            text = { Text(nat) },
                            onClick = {
                                onNationalityChange(nat)
                                expandedNationality = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = onTermsAcceptedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenSustainable,
                        uncheckedColor = DarkGreen.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "Ik ga akkoord met de algemene voorwaarden en heb het privacybeleid gelezen en begrepen.",
                    fontSize = 12.sp,
                    color = DarkGreen,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}