package dev.esan.sla_app.ui.insight.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.insight.InsightRegresionDto

@Composable
fun InsightRegresionCard(data: InsightRegresionDto) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = "Regresión Lineal (${data.tipoSla})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            Text("Pendiente: ${data.regresion.pendiente}")
            Text("Intercepto: ${data.regresion.intercepto}")
            Text("R²: ${data.regresion.r2}")

            Spacer(Modifier.height(16.dp))

            // 🔥 Gráfico de regresión completo
            InsightRegressionChart(
                historico = data.historico,
                pendiente = data.regresion.pendiente,
                intercepto = data.regresion.intercepto
            )

            Spacer(Modifier.height(20.dp))

            // Proyección
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF4F4F4))
                    .padding(12.dp)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        "Proyección (${data.proyeccion.periodoSiguiente})",
                        fontWeight = FontWeight.Bold
                    )
                    Text("Valor esperado: ${data.proyeccion.valor}")
                    Text(
                        "Riesgo: ${data.proyeccion.nivelRiesgo}",
                        color =
                            when (data.proyeccion.nivelRiesgo.lowercase()) {
                                "bajo" -> Color(0xFF28A745)
                                "medio" -> Color(0xFFFFC107)
                                else -> Color(0xFFDC3545)
                            },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Recomendación:")
            Text(
                text = data.recomendacion,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
