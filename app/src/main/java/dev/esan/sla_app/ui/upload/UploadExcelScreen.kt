package dev.esan.sla_app.ui.upload

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadExcelScreen(
    viewModel: UploadExcelViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var fileUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para el selector de archivos del sistema
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            fileUri = uri
        }
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Importar Solicitudes desde Excel") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Button(
                onClick = { launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar archivo Excel (.xlsx)")
            }

            Spacer(Modifier.height(20.dp))

            fileUri?.let { uri ->
                Text("Archivo: ${getFileName(context, uri)}")
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val file = crearArchivoTemporal(context, uri)
                        val requestFile = file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        viewModel.uploadExcel(filePart)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.loading
                ) {
                    Text("Subir Archivo")
                }
            }

            Spacer(Modifier.height(20.dp))

            if (state.loading) {
                CircularProgressIndicator()
            }

            state.success.let {
                if (it) {
                    Text("Excel importado correctamente 🎉", color = MaterialTheme.colorScheme.primary)
                }
            }

            state.error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun crearArchivoTemporal(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("No se pudo abrir el stream del archivo")
    val tempFile = File.createTempFile("upload_", ".xlsx", context.cacheDir)

    FileOutputStream(tempFile).use { output ->
        inputStream.copyTo(output)
    }
    inputStream.close()
    return tempFile
}

// Función helper para obtener el nombre del archivo desde la Uri
private fun getFileName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}