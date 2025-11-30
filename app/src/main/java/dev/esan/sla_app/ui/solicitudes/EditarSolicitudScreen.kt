package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.solicitudes.EditarSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.SolicitudDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarSolicitudScreen(
    id: Int,
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Efecto para asegurar que los datos de la solicitud están cargados.
    LaunchedEffect(key1 = id) {
        if (state.data.firstOrNull { it.id == id } == null) {
            viewModel.cargarSolicitudes()
        }
    }

    val item = state.data.firstOrNull { it.id == id }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (item != null) "Editar Solicitud #${item.id}" else "Cargando...") }) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Muestra un indicador de carga si los datos aún no están listos
            if (state.loading && item == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (item == null) {
                // Muestra un error si la solicitud no se encuentra después de cargar
                Text("Solicitud no encontrada.", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else {
                // El formulario solo se renderiza cuando 'item' está disponible
                EditForm(item = item, viewModel = viewModel, onBack = onBack)
            }
        }
    }
}

@Composable
private fun EditForm(
    item: SolicitudDto,
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit
) {
    // El estado del formulario se inicializa de forma segura aquí
    var tipoNombre by remember { mutableStateOf(item.tipoSla.nombre) }
    var ingreso by remember { mutableStateOf(item.fechaIngreso ?: "") }
    var descripcion by remember { mutableStateOf(item.descripcion) }
    var estado by remember { mutableStateOf(item.estado) }

    Column(Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = tipoNombre,
            onValueChange = { tipoNombre = it },
            label = { Text("Tipo SLA") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = ingreso, onValueChange = { ingreso = it }, label = { Text("Fecha Ingreso (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = estado, onValueChange = { estado = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.editarSolicitud(
                item.id,
                EditarSolicitudRequest(
                    tipoSla = tipoNombre,
                    fechaIngreso = ingreso.ifEmpty { null },
                    descripcion = descripcion,
                    estado = estado
                ),
                onSuccess = onBack
            )
        }) {
            Text("Guardar cambios")
        }
    }
}