package com.example.quickdropapp.composables.activities

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable

@Composable
fun AnimatedSectionHeader(title: String) {
    var animateTrigger by remember { mutableStateOf(false) }
    val animatedValue by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = { it * it * (3 - 2 * it) })
    )

    LaunchedEffect(Unit) { animateTrigger = true }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .graphicsLayer(alpha = animatedValue, scaleY = animatedValue),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(50.dp)
                .background(GreenSustainable)
                .clip(RoundedCornerShape(1.dp))
        )
    }
}