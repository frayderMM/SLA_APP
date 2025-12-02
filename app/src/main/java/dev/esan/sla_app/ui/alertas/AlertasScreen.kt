package dev.esan.sla_app.ui.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    viewModel: AlertasViewModel,
    onNavigateToEmailConfig: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alertas SLA",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A3D91)
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToEmailConfig) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Email,
                            contentDescription = "Configurar Email",
                            tint = Color(0xFF0A3D91)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF4F7FE),
                            Color(0xFFE9ECF5)
                        )
                    )
                )
        ) {

            when {
                state.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0A63C2)
                    )
                }

                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = Color(0xFFDC3545),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.data.isEmpty() -> {
                    Text(
                        text = "No hay alertas registradas",
                        color = Color(0xFF6C757D),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.data.size) { i ->
                            AlertaCard(alerta = state.data[i])
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AlertaCard(alerta: AlertaDto) {

    // ===== SEVERIDAD SEGÚN % SLA ===== //
    val severidad = when {
        alerta.porcentaje < 40 -> "High"
        alerta.porcentaje < 70 -> "Medium"
        else -> "Low"
    }

    val colorSeveridad = when (severidad) {
        "High" -> Color(0xFFDA2E2E)   // Rojo
        "Medium" -> Color(0xFF1E9A74) // Verde suave
        else -> Color(0xFF4CAF50)     // Verde fuerte
    }

    // ===== FONDO PASTEL AL ESTILO DEL EJEMPLO ===== //
    val pastelBackground = Color(0xFFF1F8ED)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = pastelBackground),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            // ======== BADGES SUPERIORES ======== //
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgeChip(text = severidad, background = colorSeveridad)
                BadgeChip(text = alerta.rol, background = Color(0xFF004D40))
                BadgeChip(text = "${alerta.porcentaje}%", background = Color(0xFF1B5E20))
            }

            Spacer(Modifier.height(14.dp))

            // ======== TÍTULO ======== //
            Text(
                text = alerta.mensaje,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3A2F),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // ======== DESCRIPCIÓN ======== //
            Text(
                text = "Esta alerta requiere atención. Revisa el SLA y toma acción lo antes posible.",
                color = Color(0xFF475348),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(14.dp))

            // ======== ICONO + FECHA ======== //
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF1E4F3E)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = alerta.fecha,
                    color = Color(0xFF2D3A32),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


@Composable
fun BadgeChip(text: String, background: Color) {
    Box(
        modifier = Modifier
            .background(background, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
