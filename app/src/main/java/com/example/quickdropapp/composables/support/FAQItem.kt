package com.example.quickdropapp.composables.support

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quickdropapp.ui.theme.DarkGreen

@Composable
fun FAQItem(question: String, answer: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(
            imageVector = Icons.Default.QuestionAnswer,
            contentDescription = "Vraag",
            tint = DarkGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = DarkGreen
            )
            Text(
                answer,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkGreen.copy(alpha = 0.7f)
            )
        }
    }
}