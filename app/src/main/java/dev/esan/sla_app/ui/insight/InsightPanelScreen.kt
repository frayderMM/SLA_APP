package dev.esan.sla_app.ui.insight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.esan.sla_app.ui.insight.components.InsightHistoricoChart
import dev.esan.sla_app.ui.insight.components.InsightIndicadoresCard
import dev.esan.sla_app.ui.insight.components.InsightRegresionCard
import dev.esan.sla_app.data.remote.dto.insight.InsightPoint

@Composable
fun InsightPanelScreen(
    viewModel: InsightPanelViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {

        // 1️⃣ Indicadores principales
        state.indicadores?.let {
            InsightIndicadoresCard(it)
        }

        // 2️⃣ Historico
        state.historico?.let { historicoDto ->
            val puntos = historicoDto.historico.mapIndexed { index, h ->
                InsightPoint(
                    x = index + 1,
                    y = h.porcentaje
                )
            }
            InsightHistoricoChart(puntos)
        }

        // 3️⃣ Regresión
        state.regresion?.let {
            InsightRegresionCard(it)
        }
    }
}
