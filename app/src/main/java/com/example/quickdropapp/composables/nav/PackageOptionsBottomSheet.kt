package com.example.quickdropapp.composables.nav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun SelectionButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(100.dp)
            .shadow(2.dp, CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) GreenSustainable else Color(0xFFE0E0E0),
            contentColor = if (isSelected) Color.White else DarkGreen
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun PackageOptionsBottomSheet(
    onConfirmSelection: (actionType: String, category: String) -> Unit
) {
    var selectedAction by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Wat wil je doen?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionButton(
                text = "Verzenden",
                icon = Icons.Filled.SubdirectoryArrowRight,
                isSelected = selectedAction == "Send",
                onClick = { selectedAction = "Send" }
            )
            Spacer(modifier = Modifier.width(16.dp))
            SelectionButton(
                text = "Ontvangen",
                icon = Icons.Filled.Inbox,
                isSelected = selectedAction == "Receive",
                onClick = { selectedAction = "Receive" }
            )
        }

        // Next Button
        val isEnabled = selectedAction != null
        Button(
            onClick = {
                if (isEnabled) {
                    val actionType = when (selectedAction) {
                        "Send" -> "send"
                        "Receive" -> "receive"
                        else -> "send"
                    }
                    val category = "package" // Always package
                    onConfirmSelection(actionType, category)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .then(
                    if (isEnabled) Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
                    else Modifier
                ),
            enabled = isEnabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled) DarkGreen else Color(0xFFE0E0E0),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Volgende",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}