package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.model.TipoSla

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
        topBar = { TopAppBar(title = { Text("Nueva Solicitud") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            var rol by remember { mutableStateOf("") }
            var fechaSolicitud by remember { mutableStateOf("") }
            var fechaIngreso by remember { mutableStateOf("") }
            var selectedSlaId by remember { mutableStateOf<Int?>(null) }

            OutlinedTextField(
                value = rol,
                onValueChange = { rol = it },
                label = { Text("Rol") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaSolicitud,
                onValueChange = { fechaSolicitud = it },
                label = { Text("Fecha Solicitud (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaIngreso,
                onValueChange = { fechaIngreso = it },
                label = { Text("Fecha Ingreso (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔥 DROPDOWN CORREGIDO
            TipoSlaDropdown(
                tiposSla = formState.tiposSla,
                selectedId = selectedSlaId,
                onSelect = { selectedSlaId = it }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    selectedSlaId?.let {
                        viewModel.createSolicitud(
                            rol, fechaSolicitud, fechaIngreso, it
                        )
                    }
                },
                enabled = !formState.isLoading
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar")
                }
            }

            formState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoSlaDropdown(
    tiposSla: List<TipoSla>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 🔥 Corrección: actualizar cuando cambie la lista del ViewModel
    val selectedText = remember(tiposSla, selectedId) {
        tiposSla.find { it.id == selectedId }?.nombre ?: "Seleccione un Tipo de SLA"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {

        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de SLA") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // 🔥 asegurado según la versión correcta
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (tiposSla.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Sin datos disponibles") },
                    onClick = { expanded = false }
                )
            } else {
                tiposSla.forEach { sla ->
                    DropdownMenuItem(
                        text = { Text("${sla.codigo} - ${sla.nombre}") },
                        onClick = {
                            onSelect(sla.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
