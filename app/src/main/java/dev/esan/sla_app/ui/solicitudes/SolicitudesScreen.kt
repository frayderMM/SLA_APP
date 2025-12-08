package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onBack: () -> Unit,
    onAddFromExcel: () -> Unit
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
                title = { 
                    Text(
                        "Gestión de Solicitudes",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    ) 
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") } }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = onAddFromExcel,
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Añadir desde Excel")
                }
                FloatingActionButton(onClick = onCrear) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Solicitud")
                }
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

// ===================================================================
// ⭐ SECCIÓN DE FILTROS MODERNA - INSPIRADA EN MATERIAL DESIGN 3
// ===================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernFilterSection(
    slaOptions: List<String>,
    selectedSla: String,
    startDate: Long?,
    endDate: Long?,
    onSlaSelected: (String) -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    val startDateText = startDate?.let { formatter.format(Date(it)) } ?: "Seleccionar"
    val endDateText = endDate?.let { formatter.format(Date(it)) } ?: "Seleccionar"

    // Dialog para fecha de inicio
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { newStart ->
                        // Validación: Si hay fecha 'Hasta', 'Desde' no puede ser posterior
                        if (endDate != null && newStart > endDate) {
                            // No hacer nada, mantener valor anterior
                        } else {
                            onDateRangeSelected(newStart, endDate)
                        }
                    }
                    showStartDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // Dialog para fecha de fin
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { newEnd ->
                        // Validación: 'Hasta' no puede ser anterior a 'Desde'
                        if (startDate != null && newEnd < startDate) {
                            // No hacer nada, mantener valor anterior
                        } else {
                            onDateRangeSelected(startDate, newEnd)
                        }
                    }
                    showEndDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título de filtros
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Filtros de Búsqueda",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            Divider(color = Color(0xFFE0E0E0))

            // Filtro de Tipo SLA
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tipo SLA:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF424242)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(slaOptions) { option ->
                        val isSelected = option == selectedSla
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSlaSelected(option) },
                            label = { Text(option) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1565C0),
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }

            Divider(color = Color(0xFFE0E0E0))

            // Filtro de Fechas
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Periodo de Solicitud:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF424242)
                )

                // Fecha de Inicio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Desde:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF616161),
                        modifier = Modifier.width(60.dp)
                    )
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (startDate != null) Color(0xFFE3F2FD) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = startDateText,
                            fontSize = 14.sp,
                            color = if (startDate != null) Color(0xFF1565C0) else Color(0xFF757575)
                        )
                    }
                }

                // Fecha de Fin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Hasta:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF616161),
                        modifier = Modifier.width(60.dp)
                    )
                    OutlinedButton(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (endDate != null) Color(0xFFE3F2FD) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = endDateText,
                            fontSize = 14.sp,
                            color = if (endDate != null) Color(0xFF1565C0) else Color(0xFF757575)
                        )
                    }
                }

                // Botón limpiar filtros
                if (startDate != null || endDate != null || selectedSla != "Todos") {
                    OutlinedButton(
                        onClick = { onDateRangeSelected(null, null); onSlaSelected("Todos") },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Limpiar Filtros", fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
    // ✅ FUNCIÓN MODIFICADA PARA ACEPTAR NULOS
    fun formatDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        return try {
            OffsetDateTime.parse(dateString).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            dateString
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Título con ID y Rol
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE3F2FD),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(
                            text = "ID: ${solicitud.id}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Rol: ${solicitud.rol}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                }

                // Tipo de SLA con badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when(solicitud.tipoSlaNombre) {
                            "SLA1" -> Color(0xFF4CAF50)
                            "SLA2" -> Color(0xFF2196F3)
                            else -> Color(0xFF9E9E9E)
                        }
                    ) {
                        Text(
                            text = solicitud.tipoSlaNombre ?: "Sin SLA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Fechas con iconos
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF757575)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Solicitud: ${formatDate(solicitud.fechaSolicitud)}",
                            fontSize = 12.sp,
                            color = Color(0xFF616161)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF757575)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ingreso: ${solicitud.fechaIngreso?.let { formatDate(it) } ?: "Pendiente"}",
                            fontSize = 12.sp,
                            color = if (solicitud.fechaIngreso != null) Color(0xFF616161) else Color(0xFFFF9800),
                            fontWeight = if (solicitud.fechaIngreso == null) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onEditar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
