package com.example.quickdropapp.composables.forms

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

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
                return if (offset <= prefix.length) 0 else offset - prefix.length
            }
        }
        return androidx.compose.ui.text.input.TransformedText(
            text = androidx.compose.ui.text.AnnotatedString(transformedText),
            offsetMapping = offsetMapping
        )
    }
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val trimmed = text.text.take(10)
        val formatted = buildString {
            trimmed.forEachIndexed { index, char ->
                append(char)
                if (index == 1 || index == 3) {
                    if (trimmed.length > index + 1) {
                        append('/')
                    }
                }
            }
        }
        val offsetMapping = object : androidx.compose.ui.text.input.OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 0 -> offset
                    offset <= 2 -> offset
                    offset <= 4 -> offset + 1
                    else -> offset + 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 0 -> offset
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else -> offset - 2
                }
            }
        }
        return androidx.compose.ui.text.input.TransformedText(
            text = androidx.compose.ui.text.AnnotatedString(formatted),
            offsetMapping = offsetMapping
        )
    }
}

@Composable
fun CourierRegistrationForm(
    onItsmeClick: () -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    mobileNumber: String,
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

    // Validation states
    val isPhoneValid = mobileNumber.matches(Regex("^\\+\\d{10,15}\$"))
    val isBirthDateValid = birthDate.matches(Regex("^\\d{2}/\\d{2}/\\d{4}\$"))
    val isNationalNumberValid = nationalNumber.matches(Regex("^\\d{11}\$"))

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
                onValueChange = { if (it.length <= 10) onBirthDateChange(it) },
                label = { Text("Geboortedatum (dd/mm/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation(),
                isError = birthDate.isNotEmpty() && !isBirthDateValid,
                supportingText = {
                    if (birthDate.isNotEmpty() && !isBirthDateValid) {
                        Text("Gebruik formaat dd/mm/yyyy", color = MaterialTheme.colorScheme.error)
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
                                    // Update mobile number with new prefix
                                    val newNumber = countryOption.phonePrefix + mobileNumber.replace(Regex("^\\+\\d+"), "")
                                    onMobileNumberChange(newNumber)
                                    expandedPhoneCountry = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.startsWith(selectedPhoneCountry.phonePrefix)) {
                            onMobileNumberChange(it)
                        } else {
                            onMobileNumberChange(selectedPhoneCountry.phonePrefix + it.replace(Regex("^\\+\\d*"), ""))
                        }
                    },
                    label = { Text("Mobiele telefoon (e.g. +32123456789)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = PrefixVisualTransformation(selectedPhoneCountry.phonePrefix),
                    isError = mobileNumber.isNotEmpty() && !isPhoneValid,
                    supportingText = {
                        if (mobileNumber.isNotEmpty() && !isPhoneValid) {
                            Text("Gebruik formaat +32xxxxxxxxx", color = MaterialTheme.colorScheme.error)
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
                        Text("Moet 11 cijfers zijn", color = MaterialTheme.colorScheme.error)
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