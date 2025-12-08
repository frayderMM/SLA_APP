package dev.esan.sla_app.ui.assistant

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

//---------------------------------------------------------
// Quick Questions (solo 4 premium)
//---------------------------------------------------------
data class QuickQuestion(val id: Int, val text: String)

val QUICK_QUESTIONS = listOf(
    QuickQuestion(1, "Estado del SLA1"),
    QuickQuestion(2, "Riesgos hoy"),
    QuickQuestion(3, "Tendencia mensual"),
    QuickQuestion(4, "Incumplimientos recientes")
)

//---------------------------------------------------------
// Main Screen
//---------------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AssistantScreen(viewModel: AssistantViewModel) {

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    val background = Brush.verticalGradient(
        listOf(
            Color(0xFF07111F),
            Color(0xFF0A2040),
            Color(0xFF093B80)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        HeaderSection()

        QuickQuestionsRowPremium(
            questions = QUICK_QUESTIONS,
            onClick = {
                input = it
                viewModel.sendMessage(it)
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 70.dp)
        ) {
            items(viewModel.messages) { msg ->
                MessageBubbleAnimated(msg)
            }
        }

        AnimatedVisibility(viewModel.isLoading.value) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4A90E2)
            )
        }

        InputBarPremium(
            input = input,
            onInputChange = { input = it },
            onSend = {
                if (input.isNotBlank()) {
                    viewModel.sendMessage(input.trim())
                    input = ""
                }
            }
        )
    }
}

//---------------------------------------------------------
// Header
//---------------------------------------------------------
@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(38.dp)
        )
        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                "Asistente SLA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Analista inteligente del desempeño",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFBFD9FF)
            )
        }
    }
}

//---------------------------------------------------------
// Quick Questions Premium
//---------------------------------------------------------
@Composable
fun QuickQuestionsRowPremium(
    questions: List<QuickQuestion>,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        questions.forEach { q ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .clickable { onClick(q.text) }
                    .border(1.dp, Color(0xFF4A90E2), RoundedCornerShape(18.dp))
            ) {
                Text(
                    q.text,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

//---------------------------------------------------------
// Input Bar Premium
//---------------------------------------------------------
@Composable
private fun InputBarPremium(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit
) {

    val voiceLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        ?.firstOrNull()
                if (!spokenText.isNullOrBlank()) onInputChange(spokenText)
            }
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color(0xFF0F203A), RoundedCornerShape(30.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            placeholder = {
                Text("Escribe o habla…", color = Color(0xFFB0B8C6))
            },
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color(0x334A90E2),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(Modifier.width(10.dp))

        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora…")
                }
                voiceLauncher.launch(intent)
            },
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF1A73E8), CircleShape)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.White)
        }

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF4A90E2), CircleShape)
                .border(2.dp, Color(0xFF81B3FF), CircleShape)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

//---------------------------------------------------------
// Message Bubble With Animation (Premium)
//---------------------------------------------------------
@Composable
fun MessageBubbleAnimated(message: ChatMessage) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically { it / 4 },
    ) {
        MessageBubblePremium(message)
    }
}

//---------------------------------------------------------
// Bubble Design Premium
//---------------------------------------------------------
@Composable
private fun MessageBubblePremium(message: ChatMessage) {

    val userBlue = Color(0xFF3D7BE8)
    val assistantGray = Color(0xFFF6F8FF)
    val errorRed = Color(0xFFB71C1C)

    val bubbleColor = when {
        message.isError -> errorRed.copy(alpha = 0.12f)
        message.isFromUser -> userBlue
        else -> assistantGray
    }

    val textColor = when {
        message.isError -> errorRed
        message.isFromUser -> Color.White
        else -> Color(0xFF0A1A2F)
    }

    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromUser) 60.dp else 0.dp,
                end = if (message.isFromUser) 0.dp else 60.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        contentAlignment = alignment
    ) {

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(bubbleColor),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(16.dp),
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )
        }
    }
}
