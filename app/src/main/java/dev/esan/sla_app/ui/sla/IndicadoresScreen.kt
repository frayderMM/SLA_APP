package dev.esan.sla_app.ui.sla

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto
import dev.esan.sla_app.ui.excel.ExportExcelViewModel
import dev.esan.sla_app.ui.excel.ExportState
import dev.esan.sla_app.ui.pdf.PdfDownloadState
import dev.esan.sla_app.ui.pdf.PdfViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.time.OffsetDateTime

// ================================================================================================
// 🔥 PANTALLA PRINCIPAL
// ================================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    indicadoresViewModel: IndicadoresViewModel,
    pdfViewModel: PdfViewModel,
    excelViewModel: ExportExcelViewModel,
    onNavigateToSolicitudes: () -> Unit
) {

    val state by indicadoresViewModel.state.collectAsState()
    val pdfState by pdfViewModel.downloadState.collectAsState()
    val excelState by excelViewModel.downloadState.collectAsState()

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var mostrarTodos by remember { mutableStateOf(false) }
    var slaSeleccionado by remember { mutableStateOf("Todos") }
    var estadoSeleccionado by remember { mutableStateOf("Todos") }

    val opcionesEstado = listOf("Todos", "Cumple", "No cumple")

    // -------------------------------------------------------------
    // 📂 HANDLER PDF
    // -------------------------------------------------------------
    LaunchedEffect(pdfState) {
        when (pdfState) {
            PdfDownloadState.Loading -> Unit

            is PdfDownloadState.Success -> {
                val body = (pdfState as PdfDownloadState.Success).body
                val uri = savePdfToFile(context, body, "Reporte_SLA_${System.currentTimeMillis()}.pdf")

                if (uri != null) {
                    scope.launch { snackbar.showSnackbar("PDF descargado correctamente") }
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        scope.launch { snackbar.showSnackbar("PDF guardado pero no se pudo abrir") }
                    }
                }

                pdfViewModel.resetDownloadState()
            }

            is PdfDownloadState.Error -> {
                scope.launch { snackbar.showSnackbar((pdfState as PdfDownloadState.Error).message) }
                pdfViewModel.resetDownloadState()
            }

            PdfDownloadState.Idle -> Unit
        }
    }

    // -------------------------------------------------------------
    // 📂 HANDLER EXCEL
    // -------------------------------------------------------------
    LaunchedEffect(excelState) {
        when (excelState) {
            ExportState.Loading -> Unit

            is ExportState.Success -> {
                val body = (excelState as ExportState.Success).body
                saveExcelToFile(context, body, "Reporte_SLA_${System.currentTimeMillis()}.xlsx")
                scope.launch { snackbar.showSnackbar("Excel descargado correctamente") }
                excelViewModel.reset()
            }

            is ExportState.Error -> {
                scope.launch { snackbar.showSnackbar((excelState as ExportState.Error).message) }
                excelViewModel.reset()
            }

            ExportState.Idle -> Unit
        }
    }

    // ============================================================================================
    // 🧩 UI PRINCIPAL
    // ============================================================================================
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Solicitudes y Reportes", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSolicitudes) {
                Icon(Icons.Default.Edit, contentDescription = "Ir a solicitudes")
            }
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F8FE))
        ) {

            when {

                state.loading && state.data.isEmpty() ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null ->
                    Text(
                        "Error: ${state.error}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )

                else -> {

                    val itemsSla = listOf("Todos") + state.tiposSla.map { it.nombre }.sorted()

                    val filtroSla =
                        if (slaSeleccionado == "Todos") state.data
                        else state.data.filter { it.tipoSla.equals(slaSeleccionado, true) }

                    val listaFiltrada =
                        when (estadoSeleccionado) {
                            "Cumple" -> filtroSla.filter { it.resultado.startsWith("Cumple", true) }
                            "No cumple" -> filtroSla.filter { it.resultado.startsWith("No cumple", true) }
                            else -> filtroSla
                        }

                    val listaMostrar =
                        if (mostrarTodos) listaFiltrada else listaFiltrada.take(10)

                    Column(Modifier.padding(16.dp)) {

                        // ==========================================================================
                        // 🔵 BOTONES PDF + EXCEL JUNTOS
                        // ==========================================================================
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1A73E8), Color(0xFF0F9D58))
                                    ),
                                    RoundedCornerShape(18.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            ElevatedButton(
                                onClick = {
                                    val codigo =
                                        if (slaSeleccionado == "Todos") null
                                        else state.tiposSla.find { it.nombre == slaSeleccionado }?.codigo

                                    pdfViewModel.downloadReport(codigo)
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, null, tint = Color(0xFF1A73E8))
                                Spacer(Modifier.width(6.dp))
                                Text("PDF", color = Color(0xFF1A73E8), fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.width(12.dp))

                            ElevatedButton(
                                onClick = { excelViewModel.exportExcel() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, null, tint = Color(0xFF0F9D58))
                                Spacer(Modifier.width(6.dp))
                                Text("Excel", color = Color(0xFF0F9D58), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // ==========================================================================
                        // 🎨 FILTROS TRANSPARENTES + DESPLEGABLE MEJORADO
                        // ==========================================================================
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            FiltroTransparentCool(
                                label = "Tipo SLA",
                                seleccion = slaSeleccionado,
                                opciones = itemsSla,
                                onSelected = { slaSeleccionado = it },
                                modifier = Modifier.weight(1f)
                            )

                            FiltroTransparentCool(
                                label = "Estado",
                                seleccion = estadoSeleccionado,
                                opciones = opcionesEstado,
                                onSelected = { estadoSeleccionado = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        LazyColumn {
                            items(listaMostrar) { item ->
                                SlaCard(item)
                                Spacer(Modifier.height(14.dp))
                            }

                            if (listaFiltrada.size > 10) {
                                item {
                                    Button(onClick = { mostrarTodos = !mostrarTodos }) {
                                        Text(if (mostrarTodos) "Ver menos" else "Ver todos")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================================================================================================
// 🔥 FUNCIÓN CARD SLA
// ================================================================================================
@Composable
fun SlaCard(item: SlaIndicadorDto) {

    fun f(fecha: String?): String =
        try { OffsetDateTime.parse(fecha).toLocalDate().toString() }
        catch (e: Exception) { fecha ?: "-" }

    fun clean(tipo: String): String =
        tipo.replace("Cumple", "", true)
            .replace("No cumple", "", true)
            .trim()

    val cumple = item.resultado.startsWith("Cumple", true)

    val colorEstado = if (cumple) Color(0xFF27AE60) else Color(0xFFC0392B)
    val pastel = if (cumple) Color(0xFFE8F6EF) else Color(0xFFFDEDEC)
    val icono = if (cumple) Icons.Default.CheckCircle else Icons.Default.Error
    val estadoTxt = if (cumple) "Cumple" else "No cumple"

    ElevatedCard(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(pastel),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(Modifier.padding(18.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.rol, fontWeight = FontWeight.Bold)
                BadgeEstado(estadoTxt, colorEstado)
            }

            Spacer(Modifier.height(12.dp))

            Text("Tipo SLA: ${clean(item.tipoSla)}")
            Text("Fecha Solicitud: ${f(item.fechaSolicitud)}")
            Text("Fecha Ingreso: ${f(item.fechaIngreso)}")

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = colorEstado)
                Spacer(Modifier.width(6.dp))
                Text("Días: ${item.dias}", fontWeight = FontWeight.Bold, color = colorEstado)
            }
        }
    }
}

@Composable
fun BadgeEstado(texto: String, fondo: Color) {
    Box(
        Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(texto, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// ================================================================================================
// 🔥 NUEVO FILTRO TRANSPARENTE Y ELEGANTE
// ================================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroTransparentCool(
    label: String,
    seleccion: String,
    opciones: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier
) {

    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {

        Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))

        Spacer(Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = seleccion,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1A237E),
                    unfocusedTextColor = Color(0xFF1A237E)
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(150)) +
                        slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(150)) +
                        slideOutVertically(tween(150)) { it / 2 }
            ) {

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            Color(0xFF90CAF9),
                            RoundedCornerShape(12.dp)
                        )
                ) {

                    opciones.forEach { opcion ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    opcion,
                                    color = Color(0xFF0D47A1),
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth(),
                            onClick = {
                                onSelected(opcion)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ================================================================================================
// 🔥 SAVE FILES
// ================================================================================================
private fun savePdfToFile(context: Context, body: ResponseBody, fileName: String): Uri? {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    uri?.let { resolver.openOutputStream(it)?.use { out -> body.byteStream().copyTo(out) } }
    return uri
}

private fun saveExcelToFile(context: Context, body: ResponseBody, fileName: String): Uri? {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(
            MediaStore.MediaColumns.MIME_TYPE,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    uri?.let { resolver.openOutputStream(it)?.use { out -> body.byteStream().copyTo(out) } }
    return uri
}
