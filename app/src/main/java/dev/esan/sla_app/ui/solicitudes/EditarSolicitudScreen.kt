package dev.esan.sla_app.ui.solicitudes

import androidx.compose.foundation.background
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
        topBar = { 
            TopAppBar(
                title = { 
                    Text(
                        "Editar Solicitud #${id}",
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

    var rol by remember { mutableStateOf(solicitud.rol) }
    var fechaSolicitud by remember { mutableStateOf(solicitud.fechaSolicitud) }
    var fechaIngreso by remember { mutableStateOf(solicitud.fechaIngreso ?: "") }
    var selectedSlaId by remember { mutableStateOf(solicitud.tipoSlaId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                
                OutlinedTextField(
                    value = fechaSolicitud,
                    onValueChange = { fechaSolicitud = it },
                    label = { Text("Fecha Solicitud (YYYY-MM-DD)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
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
                
                OutlinedTextField(
                    value = fechaIngreso,
                    onValueChange = { fechaIngreso = it },
                    label = { Text("Fecha Ingreso (YYYY-MM-DD)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Event,
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
                val fechaIngresoFinal = if (fechaIngreso.isBlank()) null else fechaIngreso
                viewModel.updateSolicitud(solicitud.id, rol, fechaSolicitud, fechaIngresoFinal, selectedSlaId)
            },
            enabled = !formState.isLoading,
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
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Text(
                        "Guardar Cambios",
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
