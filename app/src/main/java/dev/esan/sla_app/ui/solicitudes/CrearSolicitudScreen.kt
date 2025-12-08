package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
                title = { 
                    Text(
                        "Nueva Solicitud",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color(0xFF1565C0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            var rol by remember { mutableStateOf("") }
            var fechaSolicitud by remember { mutableStateOf<LocalDateTime?>(null) }
            var fechaIngreso by remember { mutableStateOf<LocalDateTime?>(null) }
            var selectedSlaId by remember { mutableStateOf<Int?>(null) }

            // Card de Información Básica
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información Básica",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    
                    OutlinedTextField(
                        value = rol,
                        onValueChange = { rol = it },
                        label = { Text("Rol") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF1565C0)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0)
                        )
                    )
                }
            }

            // Card de Fechas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Fechas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    
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
                }
            }

            // Card de Tipo SLA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Tipo de SLA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    
                    TipoSlaDropdown(
                        tiposSla = formState.tiposSla,
                        selectedId = selectedSlaId,
                        onSelect = { selectedSlaId = it }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Botón de Guardar Moderno
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    val fechaSolicitudIso = fechaSolicitud?.formatToIsoString() ?: ""
                    val fechaIngresoIso = fechaIngreso?.formatToIsoString()

                    selectedSlaId?.let {
                        viewModel.createSolicitud(rol, fechaSolicitudIso, fechaIngresoIso, it)
                    }
                },
                enabled = !formState.isLoading && rol.isNotEmpty() && fechaSolicitud != null && selectedSlaId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Text(
                            "Crear Solicitud",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            formState.error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                        Text(
                            it,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

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

    // Contenedor principal clickable con estilo moderno
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { showDatePicker = true },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            if (formattedDateTime != null) Color(0xFF1565C0) else Color(0xFFBDBDBD)
        ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = Color(0xFF1565C0)
            )
            Spacer(Modifier.width(16.dp))
            if (formattedDateTime != null) {
                Column {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1565C0),
                        fontSize = 12.sp
                    )
                    Text(
                        formattedDateTime,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF757575)
                )
            }
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
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
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                focusedLabelColor = Color(0xFF1565C0)
            )
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
