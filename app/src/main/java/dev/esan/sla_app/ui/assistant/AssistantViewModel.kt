package dev.esan.sla_app.ui.assistant

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.remote.dto.ChatRequest
import dev.esan.sla_app.data.repository.AssistantRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val repository: AssistantRepository,
    private val userId: Int,
    private val context: Context
) : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()
    val isLoading = mutableStateOf(false)

    private var lastInteraction = System.currentTimeMillis()

    init {

        // 🔵 Cargar historial desde DataStore
        viewModelScope.launch {
            ChatStorage.loadMessages(context).collectLatest { saved: List<ChatMessage> ->
                messages.clear()
                messages.addAll(saved)
            }
        }

        // 🔵 Limpieza automática por inactividad (15 minutos)
        viewModelScope.launch {
            while (true) {
                delay(60_000) // cada minuto
                val inactive = System.currentTimeMillis() - lastInteraction
                if (inactive > 15 * 60_000) {
                    clearChat()
                }
            }
        }
    }

    // 🔵 Guardar historial en DataStore
    private fun saveHistory() {
        viewModelScope.launch {
            ChatStorage.saveMessages(context, messages)
        }
    }

    // 🔵 Borrar historial
    fun clearChat() {
        viewModelScope.launch {
            ChatStorage.clear(context)
            messages.clear()
        }
    }

    // 🔵 Enviar mensaje al asistente
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        lastInteraction = System.currentTimeMillis()

        // 1. Agregar mensaje del usuario
        val userMsg = ChatMessage(text, isFromUser = true)
        messages.add(userMsg)
        saveHistory()

        // 2. Llamar API
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.sendMessage(
                    ChatRequest(message = text, userId = userId)
                )

                if (response.isSuccessful) {

                    val reply = response.body()?.reply ?: "Respuesta vacía"

                    val botMsg = ChatMessage(
                        text = reply,
                        isFromUser = false
                    )

                    messages.add(botMsg)
                } else {

                    val errorMsg = ChatMessage(
                        text = "Error ${response.code()}: ${response.message()}",
                        isFromUser = false,
                        isError = true
                    )

                    messages.add(errorMsg)
                }

            } catch (e: Exception) {

                val errorMsg = ChatMessage(
                    text = "Error: ${e.message}",
                    isFromUser = false,
                    isError = true
                )

                messages.add(errorMsg)
            }

            isLoading.value = false
            saveHistory()
        }
    }
}
