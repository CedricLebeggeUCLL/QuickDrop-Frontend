// com.example.quickdropapp.composables/CourierRegistrationForm.kt
package com.example.quickdropapp.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import com.example.quickdropapp.ui.theme.SandBeige

@Composable
fun CourierRegistrationForm(
    itsmeCode: String,
    onItsmeCodeChange: (String) -> Unit,
    licenseNumber: String,
    onLicenseNumberChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SandBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Itsme Verificatiecode
            OutlinedTextField(
                value = itsmeCode,
                onValueChange = onItsmeCodeChange,
                label = { Text("Itsme Verificatiecode") },
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

            // Rijbewijsnummer (optioneel)
            OutlinedTextField(
                value = licenseNumber,
                onValueChange = onLicenseNumberChange,
                label = { Text("Rijbewijsnummer of ID (optioneel)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenSustainable,
                    unfocusedBorderColor = DarkGreen.copy(alpha = 0.6f),
                    cursorColor = GreenSustainable,
                    focusedLabelColor = GreenSustainable
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}