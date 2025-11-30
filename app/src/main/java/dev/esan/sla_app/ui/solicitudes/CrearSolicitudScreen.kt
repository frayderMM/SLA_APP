package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.solicitudes.CrearSolicitudRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSolicitudScreen(
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit
) {
    var rol by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nueva Solicitud") }) }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {

            OutlinedTextField(value = rol, onValueChange = { rol = it }, label = { Text("Rol") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo SLA") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha Solicitud (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.crearSolicitud(
                    CrearSolicitudRequest(
                        rol = rol,
                        tipoSla = tipo,
                        fechaSolicitud = fecha,
                        descripcion = descripcion
                    ),
                    onSuccess = onBack
                )
            }) {
                Text("Guardar")
            }
        }
    }
}