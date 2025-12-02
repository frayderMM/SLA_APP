package dev.esan.sla_app.ui.insight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.esan.sla_app.ui.insight.components.InsightIndicadoresCard

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
    }
}
