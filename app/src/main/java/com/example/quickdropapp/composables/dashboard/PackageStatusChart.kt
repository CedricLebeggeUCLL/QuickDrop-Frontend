package com.example.quickdropapp.composables.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickdropapp.models.StatusCount
import com.example.quickdropapp.ui.theme.DarkGreen

@Composable
fun PackageStatusChart(statusCounts: List<StatusCount>?, navController: NavController, userId: Int) {
    val active = statusCounts?.filter { it.status in listOf("in_transit", "assigned") }?.sumOf { it.count } ?: 0
    val pending = statusCounts?.find { it.status == "pending" }?.count ?: 0
    val delivered = statusCounts?.find { it.status == "delivered" }?.count ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Pakket Status",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(
                    label = "Wachtrij",
                    value = pending.toString(),
                    color = Color(0xFF42A5F5),
                    onClick = { navController.navigate("viewPackages/$userId") }
                )
                StatusItem(
                    label = "In Werking",
                    value = active.toString(),
                    color = Color(0xFFFFA726),
                    onClick = { navController.navigate("viewPackages/$userId") }
                )
                StatusItem(
                    label = "Bezorgd",
                    value = delivered.toString(),
                    color = Color(0xFF66BB6A),
                    onClick = { navController.navigate("history/$userId") }
                )
            }
        }
    }
}