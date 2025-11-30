package dev.esan.sla_app.ui.sla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto

// ============================================================================
//  PANTALLA PRINCIPAL
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    viewModel: IndicadoresViewModel
) {
    val state by viewModel.state.collectAsState()

    var mostrarTodos by remember { mutableStateOf(false) }
    var slaSeleccionado by remember { mutableStateOf("Todos") }
    var estadoSeleccionado by remember { mutableStateOf("Todos") }

    val opcionesEstado = listOf("Todos", "Cumple", "No cumple")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Indicadores SLA",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A3D91)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF2F4FA))
        ) {

            when {
                state.loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF0A63C2)
                )

                state.error != null -> Text(
                    text = "Error: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {

                    val listaOriginal = state.data

                    // FILTRO SLA dinámico
                    val itemsSla = listOf("Todos") +
                            listaOriginal.map { it.tipoSla }.distinct().sorted()

                    // Aplicar filtro SLA
                    val filtroSla = if (slaSeleccionado == "Todos") listaOriginal
                    else listaOriginal.filter {
                        it.tipoSla.equals(slaSeleccionado, ignoreCase = true)
                    }

                    // Aplicar filtro Estado
                    val listaFiltrada = when (estadoSeleccionado) {
                        "Cumple" -> filtroSla.filter {
                            it.resultado.lowercase().startsWith("cumple ")
                        }
                        "No cumple" -> filtroSla.filter {
                            it.resultado.lowercase().startsWith("no cumple ")
                        }
                        else -> filtroSla
                    }

                    val listaMostrar =
                        if (mostrarTodos) listaFiltrada else listaFiltrada.take(10)

                    Column(modifier = Modifier.padding(16.dp)) {

                        // ========================================================================
                        // 🔥 NUEVO DISEÑO: 2 FILTROS EN UNA SOLA FILA (ELEGANTE + COMPACTO)
                        // ========================================================================
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownFiltro(
                                    label = "Tipo SLA",
                                    seleccion = slaSeleccionado,
                                    opciones = itemsSla,
                                    onSelected = { slaSeleccionado = it }
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                DropdownFiltro(
                                    label = "Estado",
                                    seleccion = estadoSeleccionado,
                                    opciones = opcionesEstado,
                                    onSelected = { estadoSeleccionado = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // LISTA DE RESULTADOS
                        LazyColumn {

                            items(listaMostrar.size) { i ->
                                SlaCard(item = listaMostrar[i])
                                Spacer(Modifier.height(14.dp))
                            }

                            // Botón ver todos / ver menos
                            item {
                                if (listaFiltrada.size > 10) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            onClick = { mostrarTodos = !mostrarTodos },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF0A63C2)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (mostrarTodos) "Ver menos" else "Ver todos",
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ============================================================================
//  CARD ESTILO PROFESIONAL (Cumple / No cumple)
// ============================================================================
@Composable
fun SlaCard(item: SlaIndicadorDto) {

    val cumple = item.resultado.lowercase().startsWith("cumple ")
    val colorEstado = if (cumple) Color(0xFF27AE60) else Color(0xFFC0392B)
    val pastelFondo = if (cumple) Color(0xFFE8F6EF) else Color(0xFFFDEDEC)
    val icono = if (cumple) Icons.Default.CheckCircle else Icons.Default.Error
    val textoEstado = if (cumple) "Cumple" else "No cumple"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = pastelFondo),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.rol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                BadgeEstado(text = textoEstado, background = colorEstado)
            }

            Spacer(Modifier.height(12.dp))

            Text("Tipo SLA: ${item.tipoSla}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha Solicitud: ${item.fechaSolicitud}", style = MaterialTheme.typography.bodySmall)
            Text("Fecha Ingreso: ${item.fechaIngreso}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = colorEstado)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Días: ${item.dias}",
                    fontWeight = FontWeight.SemiBold,
                    color = colorEstado
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = item.resultado,
                fontWeight = FontWeight.Bold,
                color = colorEstado
            )
        }
    }
}


// ============================================================================
//  BADGE ESTADO
// ============================================================================
@Composable
fun BadgeEstado(text: String, background: Color) {
    Box(
        modifier = Modifier
            .background(background, shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ============================================================================
//  DROPDOWN ESTILIZADO (REUTILIZABLE)
// ============================================================================
@Composable
fun DropdownFiltro(
    label: String,
    seleccion: String,
    opciones: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = true },
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
        ) {
            Text(
                text = "$label: $seleccion",
                color = Color(0xFF0A3D91),
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSelected(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
