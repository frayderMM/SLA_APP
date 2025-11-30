package dev.esan.sla_app.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.remote.dto.insight.InsightPoint
import dev.esan.sla_app.data.repository.InsightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsightIndicatorsViewModel(
    private val repo: InsightRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InsightIndicatorsState())
    val state: StateFlow<InsightIndicatorsState> = _state

    fun loadAll(tipo: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true)

                // 1️⃣ Indicadores principales
                val indicadores = repo.getIndicadores(tipo)

                // 2️⃣ Histórico (x,y)
                val historico = repo.getHistorico(tipo)

                // 3️⃣ Regresión
                val regresion = repo.getRegresion(tipo)

                _state.value = InsightIndicatorsState(
                    loading = false,
                    indicadores = indicadores,
                    historico = historico.historico.mapIndexed { index, item ->
                        InsightPoint(
                            x = index + 1,
                            y = item.porcentaje   // depende de tu DTO real
                        )
                    },
                    regresion = regresion
                )


            } catch (e: Exception) {
                _state.value = InsightIndicatorsState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }
}
