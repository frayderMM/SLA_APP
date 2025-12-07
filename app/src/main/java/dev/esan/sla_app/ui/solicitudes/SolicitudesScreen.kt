package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.model.Solicitud
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    viewModel: SolicitudesViewModel,
    onCrear: () -> Unit,
    onEditar: (Int) -> Unit,
    onBack: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var solicitudAEliminar by remember { mutableStateOf<Int?>(null) }

    if (showDialog && solicitudAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta solicitud?") },
            confirmButton = { Button(onClick = { solicitudAEliminar?.let { viewModel.deleteSolicitud(it) }; showDialog = false }) { Text("Eliminar") } },
            dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Solicitudes") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrear) {
                Icon(Icons.Default.Add, contentDescription = "Crear Solicitud")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                listState.isLoading && listState.solicitudes.isEmpty() -> {
                    Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                }
                listState.error != null -> {
                    Box(Modifier.fillMaxSize()) { Text(listState.error!!, Modifier.align(Alignment.Center)) }
                }
                else -> {
                    val slaOptions = listOf("Todos") + formState.tiposSla.map { it.nombre }.sorted()
                    
                    // --- SECCIÓN DE FILTROS --- 
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        FilterChipGroup(
                            items = slaOptions,
                            selectedItem = listState.slaFilter,
                            onSelected = { viewModel.onSlaFilterChanged(it) }
                        )
                        DateRangeFilter(
                            startDate = listState.startDate,
                            endDate = listState.endDate,
                            onDateRangeSelected = { start, end -> viewModel.onDateRangeSelected(start, end) }
                        )
                    }
                    
                    if (listState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listState.solicitudes) { solicitud ->
                            SolicitudCard(solicitud = solicitud, onEditar = { onEditar(solicitud.id) }, onEliminar = { solicitudAEliminar = solicitud.id; showDialog = true })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeFilter(
    startDate: Long?,
    endDate: Long?,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    val formatter = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }
    val dateText = if (startDate != null && endDate != null) {
        "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}"
    } else {
        "Todas las fechas"
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        onDateRangeSelected(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis)
                    },
                    enabled = dateRangePickerState.selectedEndDateMillis != null
                ) { Text("Aceptar") }
            },
            dismissButton = { 
                TextButton(onClick = { 
                    showDialog = false 
                    // Limpiar filtro
                    dateRangePickerState.setSelection(null, null)
                    onDateRangeSelected(null, null)
                }) { Text("Limpiar") }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, showModeToggle = true)
        }
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Filtrar por fecha:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(8.dp))
        AssistChip(
            onClick = { showDialog = true },
            label = { Text(dateText) },
            leadingIcon = { Icon(Icons.Default.Event, null) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipGroup(items: List<String>, selectedItem: String, onSelected: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) {
            val isSelected = it == selectedItem
            FilterChip(
                selected = isSelected,
                onClick = { onSelected(it) },
                label = { Text(it) },
                leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, null) } } else { null }
            )
        }
    }
}

@Composable
fun SolicitudCard(solicitud: Solicitud, onEditar: () -> Unit, onEliminar: () -> Unit) {
    fun formatDate(dateString: String): String {
        return try {
            OffsetDateTime.parse(dateString).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            dateString
        }
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ID: ${solicitud.id} - Rol: ${solicitud.rol}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = solicitud.tipoSlaNombre ?: "Sin tipo de SLA",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Solicitud: ${formatDate(solicitud.fechaSolicitud)}  •  Ingreso: ${formatDate(solicitud.fechaIngreso)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
