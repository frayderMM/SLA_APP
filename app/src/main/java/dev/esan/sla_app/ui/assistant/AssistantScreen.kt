package dev.esan.sla_app.ui.assistant

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.ui.theme.*
import kotlinx.coroutines.delay

//---------------------------------------------------------
// Quick Questions
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(assistantBackground())
    ) {

        HeaderSectionPremium()

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
                color = MaterialTheme.colorScheme.primary
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
// 🔥 CABECERA PREMIUM CON CONTRASTE
//---------------------------------------------------------
@Composable
private fun HeaderSectionPremium() {

    val c = MaterialTheme.colorScheme

    // Glow inferior dinámico
    val glow = Brush.verticalGradient(
        listOf(
            c.primary.copy(alpha = 0.85f),
            c.primary.copy(alpha = 0.12f),
            Color.Transparent
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        c.surface.copy(alpha = 0.95f),
                        c.surface.copy(alpha = 0.85f)
                    )
                ),
                RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        c.primary.copy(alpha = 0.35f),
                        c.primary.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
            )
            .drawBehind {
                drawRect(glow, size = size)
            }
            .padding(horizontal = 22.dp, vertical = 30.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Círculo con color del tema
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(c.primary, CircleShape)
                    .border(3.dp, c.onPrimary.copy(alpha = 0.85f), CircleShape)
                    .shadow(6.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = c.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    "Asistente SLA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = c.onSurface
                )
                Text(
                    "Analista inteligente del desempeño",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.onSurfaceVariant
                )
            }
        }
    }
}

//---------------------------------------------------------
// Quick Questions
//---------------------------------------------------------
@Composable
fun QuickQuestionsRowPremium(
    questions: List<QuickQuestion>,
    onClick: (String) -> Unit
) {
    val c = MaterialTheme.colorScheme

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
                    containerColor = c.surfaceVariant.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .clickable { onClick(q.text) }
                    .border(1.dp, c.primary, RoundedCornerShape(18.dp))
            ) {
                Text(
                    q.text,
                    color = c.onSurface,
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

    val c = MaterialTheme.colorScheme

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
            .background(c.surface, RoundedCornerShape(30.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            placeholder = {
                Text("Escribe o habla…", color = c.onSurfaceVariant)
            },
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.primary,
                unfocusedBorderColor = c.outline,
                cursorColor = c.primary,
                focusedTextColor = c.onSurface,
                unfocusedTextColor = c.onSurface
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
                .background(c.primary, CircleShape)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.White)
        }

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(46.dp)
                .background(c.primary, CircleShape)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

//---------------------------------------------------------
// Message Animation
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
// Bubble Design
//---------------------------------------------------------
@Composable
private fun MessageBubblePremium(message: ChatMessage) {

    val bubbleColor =
        when {
            message.isError -> assistantErrorBubble()
            message.isFromUser -> assistantUserBubble()
            else -> assistantBotBubble()
        }

    val textColor =
        if (message.isFromUser) Color.White
        else MaterialTheme.colorScheme.onSurface

    val alignment =
        if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

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
