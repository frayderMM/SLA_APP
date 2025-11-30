package dev.esan.sla_app.ui.insight.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.insight.InsightIndicadoresDto

@Composable
fun InsightIndicadoresCard(data: InsightIndicadoresDto) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Indicadores SLA (${data.tipoSla})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total", fontWeight = FontWeight.SemiBold)
                    Text("${data.total}")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cumplen", fontWeight = FontWeight.SemiBold)
                    Text("${data.cumple}", color = Color(0xFF28A745))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No cumplen", fontWeight = FontWeight.SemiBold)
                    Text("${data.noCumple}", color = Color(0xFFDC3545))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("% SLA", fontWeight = FontWeight.SemiBold)
                    Text("${data.porcentajeCumplimiento}%")
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((data.porcentajeCumplimiento / 100f).toFloat())
                        .background(
                            if (data.porcentajeCumplimiento >= 70) Color(0xFF28A745)
                            else if (data.porcentajeCumplimiento >= 50) Color(0xFFFFC107)
                            else Color(0xFFDC3545)
                        )
                )
            }
        }
    }
}
