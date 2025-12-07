package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.model.TipoSla
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSolicitudScreen(
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(formState.navigateBack) {
        if (formState.navigateBack) {
            viewModel.onDoneNavigating()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            var rol by remember { mutableStateOf("") }
            var fechaSolicitud by remember { mutableStateOf<LocalDateTime?>(null) }
            var fechaIngreso by remember { mutableStateOf<LocalDateTime?>(null) }
            var selectedSlaId by remember { mutableStateOf<Int?>(null) }

            OutlinedTextField(
                value = rol,
                onValueChange = { rol = it },
                label = { Text("Rol") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- COMPONENTE DE FECHA MEJORADO Y CORREGIDO ---
            StyledDatePicker(
                label = "Fecha de Solicitud",
                selectedDateTime = fechaSolicitud,
                onDateTimeSelected = { fechaSolicitud = it }
            )

            StyledDatePicker(
                label = "Fecha de Ingreso",
                selectedDateTime = fechaIngreso,
                onDateTimeSelected = { fechaIngreso = it }
            )

            TipoSlaDropdown(
                tiposSla = formState.tiposSla,
                selectedId = selectedSlaId,
                onSelect = { selectedSlaId = it }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                onClick = {
                    val fechaSolicitudIso = fechaSolicitud?.formatToIsoString() ?: ""
                    val fechaIngresoIso = fechaIngreso?.formatToIsoString() ?: ""

                    selectedSlaId?.let {
                        viewModel.createSolicitud(rol, fechaSolicitudIso, fechaIngresoIso, it)
                    }
                },
                enabled = !formState.isLoading && rol.isNotEmpty() && fechaSolicitud != null && fechaIngreso != null && selectedSlaId != null
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Solicitud")
                }
            }

            formState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

/**
 * Componente de UI con estilo profesional para seleccionar una fecha.
 * La hora se establece automáticamente a la hora actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyledDatePicker(
    label: String,
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDateTime = remember(selectedDateTime) {
        selectedDateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

    // Contenedor principal que es clickable
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { showDatePicker = true },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            if (formattedDateTime != null) {
                Column {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text(formattedDateTime, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
            } else {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    // Lógica del DatePickerDialog (ahora simplificada)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            // 🔥 SE COMBINA LA FECHA SELECCIONADA CON LA HORA ACTUAL
                            val finalDateTime = LocalDateTime.of(selectedDate, LocalTime.now())
                            onDateTimeSelected(finalDateTime)
                            showDatePicker = false
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun LocalDateTime.formatToIsoString(): String {
    return this.atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoSlaDropdown(
    tiposSla: List<TipoSla>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = remember(tiposSla, selectedId) {
        tiposSla.find { it.id == selectedId }?.nombre ?: "Seleccione un Tipo de SLA"
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de SLA") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (tiposSla.isEmpty()) {
                DropdownMenuItem(text = { Text("Sin datos disponibles") }, onClick = { expanded = false })
            } else {
                tiposSla.forEach { sla ->
                    DropdownMenuItem(
                        text = { Text("${sla.codigo} - ${sla.nombre}") },
                        onClick = { onSelect(sla.id); expanded = false; }
                    )
                }
            }
        }
    }
}
