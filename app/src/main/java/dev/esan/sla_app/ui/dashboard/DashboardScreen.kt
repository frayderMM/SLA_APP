package dev.esan.sla_app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.ui.insight.InsightPanelScreen
import dev.esan.sla_app.ui.insight.InsightPanelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InsightPanelViewModel,
    onNavigateToSolicitudes: () -> Unit,
    onNavigateToAlerts: () -> Unit // 🔥 1. AÑADIR NUEVO PARÁMETRO
) {
    var selectedSla by remember { mutableStateOf("SLA1") }

    LaunchedEffect(Unit) {
        viewModel.load(selectedSla)
    }

    LaunchedEffect(selectedSla) {
        viewModel.load(selectedSla)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard SLA") },
                // 🔥 2. AÑADIR LA ACCIÓN DE LA CAMPANA
                actions = {
                    IconButton(onClick = onNavigateToAlerts) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Ir a Alertas"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FC))
                .verticalScroll(rememberScrollState())
        ) {

            Button(
                onClick = onNavigateToSolicitudes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Gestionar Solicitudes")
            }

            Spacer(Modifier.height(12.dp))

            SlaSelector(
                selected = selectedSla,
                onSelect = { selectedSla = it }
            )

            Spacer(Modifier.height(16.dp))

            InsightPanelScreen(viewModel)
        }
    }
}

@Composable
fun SlaSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SLA Seleccionado: $selected")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("SLA1", "SLA2").forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}