package dev.esan.sla_app.ui.assistant


import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import java.util.Locale

object VoiceToTextHelper {

    fun startVoiceRecognition(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora…")
        }

        launcher.launch(intent)
    }
}
