package dev.esan.sla_app.ui.insight.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.insight.InsightPoint

@Composable
fun InsightRegressionChart(
    historico: List<InsightPoint>,
    pendiente: Double,
    intercepto: Double
) {
    if (historico.isEmpty()) {
        Text("Sin datos históricos", modifier = Modifier.padding(16.dp))
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                "Gráfico de Regresión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {

                    val maxY = maxOf(
                        1f,
                        historico.maxOf { it.y }.toFloat()
                    )
                    val minY = 0f

                    val spacing = 60f
                    val widthStep = (size.width - spacing) / (historico.size - 1)

                    val path = Path()

                    // 🔵 Línea azul (puntos reales)
                    historico.forEachIndexed { index, point ->

                        val x = index * widthStep + spacing
                        val yRatio = ((point.y).toFloat() - minY) / (maxY - minY)
                        val y = size.height - (yRatio * size.height)

                        if (index == 0) path.moveTo(x, y)
                        else path.lineTo(x, y)

                        drawCircle(
                            color = Color(0xFF0D47A1),
                            radius = 7f,
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF0D47A1),
                        style = Stroke(width = 4f)
                    )

                    // 🔴 Línea roja (regresión lineal)
                    val x1 = spacing
                    val x2 = size.width

                    val y1Pred = (pendiente * 1 + intercepto).toFloat()
                    val y2Pred = (pendiente * historico.size + intercepto).toFloat()

                    val y1 = size.height - ((y1Pred - minY) / (maxY - minY)) * size.height
                    val y2 = size.height - ((y2Pred - minY) / (maxY - minY)) * size.height

                    drawLine(
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        color = Color.Red,
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}
