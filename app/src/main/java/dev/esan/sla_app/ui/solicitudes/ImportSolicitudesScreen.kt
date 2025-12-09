package dev.esan.sla_app.ui.solicitudes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSolicitudesScreen(
    viewModel: ImportExcelViewModel,
    onBack: () -> Unit = {}
) {
    var selectedFile by remember { mutableStateOf<Uri?>(null) }

    val state by viewModel.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedFile = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importar Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = { launcher.launch("*/*") }) {
                Text("Seleccionar archivo Excel")
            }

            Spacer(modifier = Modifier.height(20.dp))

            selectedFile?.let {
                Text("Archivo seleccionado ✔️")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    selectedFile?.let { viewModel.upload(it) }
                },
                enabled = selectedFile != null
            ) {
                Text("Subir archivo")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ===============================
            // ESTADOS
            // ===============================
            when (state) {

                is ExcelState.Loading -> CircularProgressIndicator()

                is ExcelState.Success -> {
                    val data = state as ExcelState.Success
                    Text("Éxito: ${data.message}")
                    if (data.errores.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Errores:")
                        data.errores.forEach { Text("- $it") }
                    }
                }

                is ExcelState.Error -> {
                    val err = state as ExcelState.Error
                    Text("Error: ${err.message}", color = MaterialTheme.colorScheme.error)
                }

                else -> {}
            }
        }
    }
}
