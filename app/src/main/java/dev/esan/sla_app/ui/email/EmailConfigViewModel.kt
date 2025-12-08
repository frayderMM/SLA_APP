package dev.esan.sla_app.ui.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.EmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmailConfigState(
    val email: String = "",
    val subject: String = "",
    val customMessage: String = "",
    val intervalHours: Long = 24,
    val isScheduled: Boolean = false,
    val scheduleMode: ScheduleMode = ScheduleMode.INTERVAL,
    val dailyHour: String = "08",
    val dailyMinute: String = "00",
    val isLoading: Boolean = false,
    val message: String? = null
)

enum class ScheduleMode {
    INTERVAL, DAILY
}

class EmailConfigViewModel(
    private val repository: EmailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmailConfigState())
    val state: StateFlow<EmailConfigState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onSubjectChange(subject: String) {
        _state.update { it.copy(subject = subject) }
    }

    fun onCustomMessageChange(message: String) {
        _state.update { it.copy(customMessage = message) }
    }

    fun onIntervalChange(hours: Long) {
        _state.update { it.copy(intervalHours = hours) }
    }

    fun onScheduleModeChange(mode: ScheduleMode) {
        _state.update { it.copy(scheduleMode = mode) }
    }

    fun onDailyTimeChange(hour: String, minute: String) {
        _state.update { it.copy(dailyHour = hour, dailyMinute = minute) }
    }

    fun onScheduleToggle(enabled: Boolean) {
        _state.update { it.copy(isScheduled = enabled) }
        applySchedule()
    }

    fun applySchedule() {
        if (_state.value.isScheduled) {
            if (_state.value.email.isNotBlank()) {
                if (_state.value.scheduleMode == ScheduleMode.INTERVAL) {
                    repository.scheduleEmailReport(
                        _state.value.intervalHours, 
                        _state.value.email,
                        _state.value.subject,
                        _state.value.customMessage
                    )
                    _state.update { it.copy(message = "Reporte programado cada ${_state.value.intervalHours} horas") }
                } else {
                    val h = _state.value.dailyHour.toIntOrNull()
                    val m = _state.value.dailyMinute.toIntOrNull()

                    if (h != null && m != null && h in 0..23 && m in 0..59) {
                        repository.scheduleDailyReport(
                            h, m, 
                            _state.value.email,
                            _state.value.subject,
                            _state.value.customMessage
                        )
                        val timeStr = String.format("%02d:%02d", h, m)
                        _state.update { it.copy(message = "Reporte programado diariamente a las $timeStr") }
                    } else {
                        _state.update { it.copy(message = "Hora inválida. Ingrese hora (0-23) y minuto (0-59)") }
                    }
                }
            } else {
                _state.update { it.copy(isScheduled = false, message = "Ingrese un correo válido") }
            }
        } else {
            repository.cancelScheduledReport()
            _state.update { it.copy(message = "Programación cancelada") }
        }
    }

    fun sendReportNow() {
        val email = _state.value.email
        if (email.isBlank()) {
            _state.update { it.copy(message = "Ingrese un correo válido") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            val result = repository.sendEmailNow(
                email, 
                _state.value.subject, 
                _state.value.customMessage
            )
            _state.update { it.copy(isLoading = false) }
            
            result.onSuccess {
                _state.update { it.copy(message = "Reporte enviado correctamente") }
            }.onFailure { e ->
                _state.update { it.copy(message = "Error al enviar: ${e.message}") }
            }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }
}

class EmailConfigViewModelFactory(private val repository: EmailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmailConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmailConfigViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
