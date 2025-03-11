// com.example.quickdropapp.composables/AddressInputField.kt
package com.example.quickdropapp.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = DarkGreen.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address.street_name,
            onValueChange = { onAddressChange(address.copy(street_name = it)) },
            label = { Text("$label (Straat)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                cursorColor = GreenSustainable,
                focusedLabelColor = GreenSustainable
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address.house_number,
            onValueChange = { onAddressChange(address.copy(house_number = it)) },
            label = { Text("$label (Huisnummer)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                cursorColor = GreenSustainable,
                focusedLabelColor = GreenSustainable
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address.postal_code,
            onValueChange = { onAddressChange(address.copy(postal_code = it)) },
            label = { Text("$label (Postcode)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenSustainable,
                unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                cursorColor = GreenSustainable,
                focusedLabelColor = GreenSustainable
            ),
            shape = RoundedCornerShape(12.dp)
        )
        // Extra info veld is verwijderd om lege velden te voorkomen
    }
}