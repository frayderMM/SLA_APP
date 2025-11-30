package dev.esan.sla_app.ui.insight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.ui.insight.components.*
import dev.esan.sla_app.data.remote.dto.insight.InsightPoint

@Composable
fun InsightIndicatorsSection(
    viewModel: InsightIndicatorsViewModel
) {
    val state by viewModel.state.collectAsState()

    when {
        state.loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }

        state.error != null -> {
            Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        state.indicadores != null -> {
            Column(Modifier.fillMaxSize()) {

                // ========= TARJETAS PRINCIPALES =========
                InsightIndicadoresCard(
                    data = state.indicadores!!
                )

                // ========= HISTÓRICO SLA (si existe) =========
                if (state.historico != null && state.historico!!.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    InsightHistoricoChart(
                        historico = state.historico!!  // ← YA ES List<InsightPoint>
                    )
                }

                // ========= REGRESIÓN (si existe) =========
                if (state.regresion != null) {
                    Spacer(Modifier.height(12.dp))

                    InsightRegresionCard(
                        data = state.regresion!!
                    )
                }
            }
        }
    }
}
