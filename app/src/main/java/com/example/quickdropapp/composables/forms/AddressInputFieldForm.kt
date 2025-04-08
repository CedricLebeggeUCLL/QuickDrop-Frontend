package com.example.quickdropapp.composables.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.Address
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest

@Composable
fun AddressInputField(
    label: String,
    address: Address,
    onAddressChange: (Address) -> Unit,
    isEditable: Boolean = true
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var inputText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(listOf<com.google.android.libraries.places.api.model.AutocompletePrediction>()) }
    var showDropdown by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.titleMedium, color = DarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        if (isEditable) {
            TextField(
                value = inputText,
                onValueChange = { newText ->
                    inputText = newText
                    if (newText.isNotEmpty()) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newText)
                            .setCountries("BE") // Beperk tot België
                            .build()
                        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                            suggestions = response.autocompletePredictions
                            showDropdown = suggestions.isNotEmpty()
                        }.addOnFailureListener { exception ->
                            // Log fout of toon een melding
                        }
                    } else {
                        suggestions = emptyList()
                        showDropdown = false
                    }
                },
                label = { Text("Zoek adres") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                )
            )

            if (showDropdown) {
                Column {
                    suggestions.forEach { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val placeId = prediction.placeId
                                    val request = FetchPlaceRequest.builder(
                                        placeId,
                                        listOf(Place.Field.ADDRESS_COMPONENTS)
                                    ).build()
                                    placesClient.fetchPlace(request).addOnSuccessListener { response ->
                                        val place = response.place
                                        val components = place.addressComponents?.asList() ?: emptyList()
                                        var street = ""
                                        var houseNumber = ""
                                        var postalCode = ""
                                        var city = ""
                                        var country = ""
                                        components.forEach { component ->
                                            when {
                                                component.types.contains("route") -> street = component.name
                                                component.types.contains("street_number") -> houseNumber = component.name
                                                component.types.contains("postal_code") -> postalCode = component.name
                                                component.types.contains("locality") -> city = component.name
                                                component.types.contains("country") -> country = component.name
                                            }
                                        }
                                        val parsedAddress = Address(
                                            street_name = street,
                                            house_number = houseNumber,
                                            postal_code = postalCode,
                                            city = city,
                                            country = country
                                        )
                                        onAddressChange(parsedAddress)
                                        inputText = "$street $houseNumber, $postalCode $city, $country"
                                        showDropdown = false
                                    }.addOnFailureListener { exception ->
                                        // Log fout of toon een melding
                                    }
                                }
                                .padding(8.dp),
                            fontSize = 16.sp,
                            color = DarkGreen
                        )
                    }
                }
            }
        } else {
            // Alleen-lezen versie
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = GreenSustainable,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Adres",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${address.street_name} ${address.house_number}, ${address.postal_code} ${address.city}, ${address.country}",
                        fontSize = 16.sp,
                        color = DarkGreen
                    )
                }
            }
            if (!address.extra_info.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = GreenSustainable,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Extra info",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                        Text(
                            text = address.extra_info,
                            fontSize = 16.sp,
                            color = DarkGreen
                        )
                    }
                }
            }
        }
    }
}