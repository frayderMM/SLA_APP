package dev.esan.sla_app.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.InsightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InsightPanelViewModel(
    private val repo: InsightRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InsightPanelState())
    val state: StateFlow<InsightPanelState> = _state

    fun load(tipo: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, tipoSla = tipo, error = null) }

            try {
                val ind = repo.getIndicadores(tipo)
                val hist = repo.getHistorico(tipo)
                val reg = repo.getRegresion(tipo)

                _state.update {
                    it.copy(
                        loading = false,
                        indicadores = ind,
                        historico = hist,
                        regresion = reg
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        loading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}
