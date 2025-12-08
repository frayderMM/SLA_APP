package dev.esan.sla_app.ui.assistant

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ---------------- DATOS DUMMY ----------------

private data class PreviewMessage(
    val text: String,
    val isFromUser: Boolean,
    val isError: Boolean = false
)

private val dummyMessages = listOf(
    PreviewMessage("¿Cuántas solicitudes incumplí esta semana?", true),
    PreviewMessage("En los últimos 7 días incumpliste 2 SLA.", false),
    PreviewMessage("¿Cuál fue mi tendencia del mes?", true),
    PreviewMessage("Tu tendencia general es estable con ligera mejora.", false),
    PreviewMessage("No se pudo conectar al servidor. Inténtalo más tarde.", false, true)
)

// ---------------- UI PRINCIPAL ----------------

@Composable
fun AssistantPreviewScreen() {

    var input by remember { mutableStateOf("") }
    val isLoading by remember { mutableStateOf(false) }

    // 🎨 Fondo degradado profesional
    val backgroundBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF07111F),   // Azul super oscuro
            Color(0xFF0A2345),   // Azul medianamente profundo
            Color(0xFF0D3C7A)    // Azul fuerte
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // ---------------- HEADER ----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column {
                Text(
                    text = "Asistente SLA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tu analista inteligente de desempeño",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB8CFFF)
                )
            }
        }

        // ---------------- MENSAJES ----------------
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(dummyMessages) { message ->
                MessageBubbleEnhanced(message)
            }
        }

        // ---------------- LOADING ----------------
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4A90E2)
            )
        }

        // ---------------- INPUT BAR ----------------
        InputBar(
            input = input,
            onInputChange = { input = it },
            onSend = { /* send action */ }
        )
    }
}

// ------------------------------------------------------------------
// 🔵 BARRA DE ESCRITURA PROFESIONAL
// ------------------------------------------------------------------

@Composable
private fun InputBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Caja moderna estilo ChatGPT
        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            placeholder = { Text("Escribe tu mensaje…", color = Color(0xFFB0B8C6)) },
            modifier = Modifier
                .weight(1f)
                .shadow(6.dp, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.White
            )
        )

        Spacer(Modifier.width(10.dp))

        // Botón circular con glow azul
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF4A90E2), CircleShape)
                .border(
                    width = 2.dp,
                    color = Color(0xFF81B3FF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// 💬 BURBUJAS MEJORADAS TIPO CHATGPT / IMESSAGE
// ------------------------------------------------------------------

@Composable
private fun MessageBubbleEnhanced(message: PreviewMessage) {

    val userBlue = Color(0xFF3D7BE8)
    val assistantGray = Color(0xFFF6F8FF)
    val errorRed = Color(0xFFB71C1C)

    val bubbleColor = when {
        message.isError -> errorRed.copy(alpha = 0.12f)
        message.isFromUser -> userBlue
        else -> assistantGray.copy(alpha = 0.98f)
    }

    val textColor = when {
        message.isError -> errorRed
        message.isFromUser -> Color.White
        else -> Color(0xFF0A1A2F)
    }

    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val horizontalPadding = if (message.isFromUser) 50.dp else 0.dp
    val oppositePadding = if (message.isFromUser) 0.dp else 50.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = oppositePadding, top = 6.dp, bottom = 6.dp),
        contentAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.animateContentSize()
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ---------------- PREVIEW ----------------

@Preview(showBackground = true)
@Composable
fun AssistantPreviewScreenPreview() {
    AssistantPreviewScreen()
}
