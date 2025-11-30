package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.model.Solicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    viewModel: SolicitudesViewModel,
    onCrear: () -> Unit,
    onEditar: (Int) -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val formState by viewModel.formState.collectAsState() // 🔥 1. OBTENER EL ESTADO DEL FORMULARIO

    var showDialog by remember { mutableStateOf(false) }
    var solicitudAEliminar by remember { mutableStateOf<Int?>(null) }

    // Dialogo de confirmación para eliminar
    if (showDialog && solicitudAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta solicitud?") },
            confirmButton = {
                Button(onClick = {
                    solicitudAEliminar?.let { viewModel.deleteSolicitud(it) }
                    showDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        // 🔥 2. AÑADIR TOPAPPBAR
        topBar = { TopAppBar(title = { Text("Gestión de Solicitudes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrear) {
                Icon(Icons.Default.Add, contentDescription = "Crear Solicitud")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            when {
                listState.isLoading && listState.solicitudes.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                listState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(listState.error!!, modifier = Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    // 🔥 3. OPCIONES PARA EL FILTRO
                    val slaOptions = listOf("Todos") + formState.tiposSla.map { it.nombre }.sorted()

                    Spacer(Modifier.height(16.dp))

                    // 🔥 4. AÑADIR DROPDOWN DE FILTRO
                    DropdownFiltro(
                        label = "Tipo SLA",
                        seleccion = listState.slaFilter,
                        opciones = slaOptions,
                        onSelected = { newSla -> viewModel.onSlaFilterChanged(newSla) }
                    )

                    Spacer(Modifier.height(16.dp))

                    if (listState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listState.solicitudes) { solicitud ->
                            SolicitudCard(
                                solicitud = solicitud,
                                onEditar = { onEditar(solicitud.id) },
                                onEliminar = {
                                    solicitudAEliminar = solicitud.id
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: Solicitud,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${solicitud.id}", fontWeight = FontWeight.Bold)
            Text("Rol: ${solicitud.rol}")
            Text("Fecha Solicitud: ${solicitud.fechaSolicitud}")
            Text("Fecha Ingreso: ${solicitud.fechaIngreso}")
            Text("Tipo SLA: ${solicitud.tipoSlaNombre ?: "N/A"}")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditar) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar")
                }
                Button(onClick = onEliminar, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

// 🔥 5. COMPOSABLE REUTILIZABLE PARA EL FILTRO
@Composable
private fun DropdownFiltro(
    label: String,
    seleccion: String,
    opciones: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { expanded = true }) {
            Text("$label: $seleccion", fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = {
                    onSelected(opcion)
                    expanded = false
                })
            }
        }
    }
}
