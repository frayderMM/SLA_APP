package dev.esan.sla_app.ui.insight.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.insight.InsightHistoricoItem

import androidx.compose.ui.graphics.drawscope.Stroke   // ✔ IMPORT CORRECTO

import dev.esan.sla_app.data.remote.dto.insight.InsightPoint

@Composable
fun InsightHistoricoChart(historico: List<InsightPoint>) {

    if (historico.isEmpty()) {
        Text("Sin datos históricos", modifier = Modifier.padding(16.dp))
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                "Histórico SLA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {

                    val maxY = historico.maxOf { it.y }.toFloat().coerceAtLeast(1f)
                    val spacing = 60f
                    val widthStep = (size.width - spacing) / (historico.size - 1)

                    val path = Path()

                    historico.forEachIndexed { index, point ->

                        val x = index * widthStep + spacing
                        val yRatio = (point.y.toFloat()) / maxY
                        val y = size.height - (yRatio * size.height)

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }

                        drawCircle(
                            color = Color(0xFF1565C0),
                            radius = 6f,
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF1565C0),
                        style = Stroke(4f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                historico.forEach { item ->
                    Text("T${item.x}")
                }
            }
        }
    }
}
