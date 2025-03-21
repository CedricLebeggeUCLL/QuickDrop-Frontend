package com.example.quickdropapp.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.Address
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun AddressInputField(
    label: String,
    address: Address,
    onAddressChange: (Address) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.street_name,
                onValueChange = { onAddressChange(address.copy(street_name = it)) },
                label = { Text("Straatnaam", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                isError = address.street_name.isBlank()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Numbers,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.house_number,
                onValueChange = { onAddressChange(address.copy(house_number = it)) },
                label = { Text("Huisnummer", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                isError = address.house_number.isBlank()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.PinDrop,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.postal_code,
                onValueChange = { onAddressChange(address.copy(postal_code = it)) },
                label = { Text("Postcode", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                isError = address.postal_code.isBlank()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationCity,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.city ?: "",
                onValueChange = { onAddressChange(address.copy(city = it)) },
                label = { Text("Stad", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                isError = address.city.isNullOrBlank()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Public,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.country ?: "",
                onValueChange = { onAddressChange(address.copy(country = it)) },
                label = { Text("Land", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                isError = address.country.isNullOrBlank()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = GreenSustainable,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = address.extra_info ?: "",
                onValueChange = { newValue ->
                    onAddressChange(address.copy(extra_info = if (newValue.isBlank()) null else newValue))
                },
                label = { Text("Extra info (optioneel)", fontSize = 14.sp, color = DarkGreen.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenSustainable,
                    unfocusedIndicatorColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                )
            )
        }
    }
}