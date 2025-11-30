package dev.esan.sla_app.ui.sla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.SlaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndicadoresViewModel(
    private val repo: SlaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IndicadoresState())
    val state: StateFlow<IndicadoresState> = _state.asStateFlow()

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            try {
                _state.value = IndicadoresState(loading = true)

                val response = repo.cargarIndicadores()

                _state.value = IndicadoresState(
                    loading = false,
                    data = response
                )

            } catch (e: Exception) {
                _state.value = IndicadoresState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }
}