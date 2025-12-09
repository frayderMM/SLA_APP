package dev.esan.sla_app.ui.sla

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto
import dev.esan.sla_app.ui.pdf.PdfDownloadState
import dev.esan.sla_app.ui.pdf.PdfViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    indicadoresViewModel: IndicadoresViewModel,
    pdfViewModel: PdfViewModel,
    onNavigateToSolicitudes: () -> Unit
) {
    val state by indicadoresViewModel.state.collectAsState()
    val pdfState by pdfViewModel.downloadState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var mostrarTodos by remember { mutableStateOf(false) }
    var slaSeleccionado by remember { mutableStateOf("Todos") } // Guarda el NOMBRE, ej: "SLA Nivel 1"
    var estadoSeleccionado by remember { mutableStateOf("Todos") }

    val opcionesEstado = listOf("Todos", "Cumple", "No cumple")

    LaunchedEffect(pdfState) {
        when (pdfState) {

            PdfDownloadState.Loading -> Unit

            is PdfDownloadState.Success -> {
                val success = pdfState as PdfDownloadState.Success
                val body = success.body

                val uri = savePdfToFile(
                    context = context,
                    body = body,
                    fileName = "Reporte_SLA_${System.currentTimeMillis()}.pdf"
                )

                if (uri != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar("PDF descargado correctamente.")
                    }

                    // Abrir PDF automáticamente
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)

                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Archivo guardado, pero no se pudo abrir.")
                        }
                    }

                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error al guardar el PDF.")
                    }
                }

                pdfViewModel.resetDownloadState()
            }

            is PdfDownloadState.Error -> {
                val err = pdfState as PdfDownloadState.Error
                scope.launch { snackbarHostState.showSnackbar(err.message) }
                pdfViewModel.resetDownloadState()
            }

            PdfDownloadState.Idle -> Unit
        }
    }



    Scaffold(
        topBar = { TopAppBar(title = { Text("Indicadores y Reportes", fontWeight = FontWeight.Bold) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { 
            FloatingActionButton(onClick = onNavigateToSolicitudes) {
                Icon(Icons.Default.Edit, contentDescription = "Gestionar Solicitudes")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF2F4FA))
        ) {
            when {
                state.loading && state.data.isEmpty() -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> Text("Error: ${state.error}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                else -> {
                    val itemsSla = listOf("Todos") + state.tiposSla.map { it.nombre }.sorted()
                    val listaOriginal = state.data

                    // 🔥 LA CORRECCIÓN DEFINITIVA: Comparar nombre con nombre
                    val filtroSla = if (slaSeleccionado == "Todos") {
                        listaOriginal
                    } else {
                        listaOriginal.filter { it.tipoSla.equals(slaSeleccionado, ignoreCase = true) }
                    }

                    val listaFiltrada = when (estadoSeleccionado) {
                        "Cumple" -> filtroSla.filter { it.resultado.lowercase().startsWith("cumple ") }
                        "No cumple" -> filtroSla.filter { it.resultado.lowercase().startsWith("no cumple ") }
                        else -> filtroSla
                    }
                    val listaMostrar = if (mostrarTodos) listaFiltrada else listaFiltrada.take(10)

                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                // La lógica del botón de descarga sigue siendo correcta: convierte nombre a código.
                                val codigoSlaParaReporte = if (slaSeleccionado == "Todos") {
                                    null
                                } else {
                                    state.tiposSla.find { it.nombre == slaSeleccionado }?.codigo
                                }
                                pdfViewModel.downloadReport(codigoSlaParaReporte)
                            },
                            enabled = pdfState !is PdfDownloadState.Loading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                             if (pdfState is PdfDownloadState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.CloudDownload, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Descargar Reporte PDF", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DropdownFiltro(
                                label = "Tipo SLA",
                                seleccion = slaSeleccionado,
                                opciones = itemsSla,
                                onSelected = { slaSeleccionado = it },
                                modifier = Modifier.weight(1f)
                            )
                            DropdownFiltro(
                                label = "Estado",
                                seleccion = estadoSeleccionado,
                                opciones = opcionesEstado,
                                onSelected = { estadoSeleccionado = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        LazyColumn {
                            items(listaMostrar) { item ->
                                SlaCard(item = item)
                                Spacer(Modifier.height(14.dp))
                            }
                            if (listaFiltrada.size > 10) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Button(onClick = { mostrarTodos = !mostrarTodos }) {
                                            Text(if (mostrarTodos) "Ver menos" else "Ver todos")
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun savePdfToFile(context: Context, body: ResponseBody, fileName: String): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            body.byteStream().use { inputStream ->
                inputStream.copyTo(outputStream!!)
            }
        }
    }
    return uri
}

private fun formatFecha(fecha: String): String {
    return fecha.substringBefore("T")
}

@Composable
fun SlaCard(item: SlaIndicadorDto) {
    val cumple = item.resultado.lowercase().startsWith("cumple ")
    val colorEstado = if (cumple) Color(0xFF27AE60) else Color(0xFFC0392B)
    val pastelFondo = if (cumple) Color(0xFFE8F6EF) else Color(0xFFFDEDEC)
    val icono = if (cumple) Icons.Default.CheckCircle else Icons.Default.Error
    val textoEstado = if (cumple) "Cumple" else "No cumple"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = pastelFondo),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.rol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                BadgeEstado(text = textoEstado, background = colorEstado)
            }
            Spacer(Modifier.height(12.dp))
            Text("Tipo SLA: ${item.tipoSla}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha Solicitud: ${formatFecha(item.fechaSolicitud)}", style = MaterialTheme.typography.bodySmall)
            Text("Fecha Ingreso: ${formatFecha(item.fechaIngreso)}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = colorEstado)
                Spacer(Modifier.width(6.dp))
                Text("Días: ${item.dias}", fontWeight = FontWeight.SemiBold, color = colorEstado)
            }
        }
    }
}

@Composable
fun BadgeEstado(text: String, background: Color) {
    Box(
        modifier = Modifier
            .background(background, shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltro(
    label: String,
    seleccion: String,
    opciones: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSelected(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
