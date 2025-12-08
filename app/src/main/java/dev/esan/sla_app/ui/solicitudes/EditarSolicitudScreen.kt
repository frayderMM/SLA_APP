package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.model.Solicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarSolicitudScreen(
    id: Int,
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val solicitudAEditar = remember(listState.solicitudes, id) {
        listState.solicitudes.find { it.id == id }
    }

    // Navegar hacia atrás cuando la operación sea exitosa
    LaunchedEffect(formState.navigateBack) {
        if (formState.navigateBack) {
            viewModel.onDoneNavigating()
            onBack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Solicitud #${id}") }) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (solicitudAEditar == null) {
                // Muestra un indicador de carga o un error si la solicitud no se encuentra
                if (listState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Text("Error: Solicitud no encontrada", modifier = Modifier.align(Alignment.Center))
                }
            } else {
                // Muestra el formulario una vez que se encuentra la solicitud
                EditForm(solicitud = solicitudAEditar, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun EditForm(
    solicitud: Solicitud,
    viewModel: SolicitudesViewModel
) {
    val formState by viewModel.formState.collectAsState()

    // ✅ CORRECCIÓN: Se manejan las fechas nulas usando el operador Elvis (`?: ""`)
    var rol by remember { mutableStateOf(solicitud.rol) }
    var fechaSolicitud by remember { mutableStateOf(solicitud.fechaSolicitud ?: "") }
    var fechaIngreso by remember { mutableStateOf(solicitud.fechaIngreso ?: "") }
    var selectedSlaId by remember { mutableStateOf(solicitud.tipoSlaId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = rol, onValueChange = { rol = it }, label = { Text("Rol") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fechaSolicitud, onValueChange = { fechaSolicitud = it }, label = { Text("Fecha Solicitud (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fechaIngreso, onValueChange = { fechaIngreso = it }, label = { Text("Fecha Ingreso (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())

        // Dropdown para Tipos de SLA, pre-seleccionando el valor actual
        TipoSlaDropdown(
            tiposSla = formState.tiposSla,
            selectedId = selectedSlaId,
            onSelect = { selectedSlaId = it }
        )


        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.updateSolicitud(solicitud.id, rol, fechaSolicitud, fechaIngreso, selectedSlaId)
            },
            enabled = !formState.isLoading
        ) {
            if (formState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar Cambios")
            }
        }

        formState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
