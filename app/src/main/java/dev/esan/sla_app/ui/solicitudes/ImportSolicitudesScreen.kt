package dev.esan.sla_app.ui.solicitudes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSolicitudesScreen(
    onBack: () -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para el selector de archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedFileUri = uri
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importar desde Excel") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Selecciona un archivo de Excel (.xlsx) para importar las solicitudes en lote.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Muestra el nombre del archivo si se seleccionó uno
            if (selectedFileUri != null) {
                Text(
                    text = "Archivo: ${selectedFileUri?.path?.substringAfterLast('/')}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(onClick = {
                // Abre el selector de archivos de tipo Excel
                filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }) {
                Text("Seleccionar archivo")
            }
        }
    }
}
