package com.example.quickdropapp.composables.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun StatItem(label: String, value: String, animatedValue: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenSustainable,
            modifier = Modifier.graphicsLayer(alpha = animatedValue)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = DarkGreen.copy(alpha = 0.8f)
        )
    }
}