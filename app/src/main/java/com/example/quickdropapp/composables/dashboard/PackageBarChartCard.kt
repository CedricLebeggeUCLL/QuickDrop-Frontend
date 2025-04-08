package com.example.quickdropapp.composables.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickdropapp.models.MonthlyCount
import com.example.quickdropapp.ui.theme.DarkGreen
import com.example.quickdropapp.ui.theme.GreenSustainable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PackageBarChartCard(packagesPerMonth: List<MonthlyCount>?) {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMM ''yy", java.util.Locale.getDefault())

    val months = List(6) { i ->
        val date = currentDate.minusMonths(i.toLong())
        date.format(formatter)
    }.reversed()

    val data = months.map { label ->
        val date = LocalDate.parse("01 $label", DateTimeFormatter.ofPattern("dd MMM ''yy"))
        val month = date.monthValue
        val year = date.year
        packagesPerMonth?.find { it.month == month && it.year == year }?.count?.toFloat() ?: 0f
    }

    val maxValue = data.maxOrNull() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Pakketten per Maand",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)) {
                val barWidth = size.width / 6
                data.forEachIndexed { index, value ->
                    val barHeight = if (maxValue > 0) (value / maxValue) * size.height * 0.85f else 0f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(GreenSustainable, GreenSustainable.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(index * barWidth + barWidth * 0.1f, size.height - barHeight),
                        size = Size(barWidth * 0.8f, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            months[index],
                            index * barWidth + barWidth / 2,
                            size.height + 40f,
                            android.graphics.Paint().apply {
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                        drawText(
                            value.toInt().toString(),
                            index * barWidth + barWidth / 2,
                            size.height - barHeight - 10f,
                            android.graphics.Paint().apply {
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.parseColor("#006400")
                            }
                        )
                    }
                }
            }
        }
    }
}