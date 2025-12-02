package dev.esan.sla_app.ui.email

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailConfigScreen(
    viewModel: EmailConfigViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.message) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Configuración de Reportes",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A3D91)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF0A3D91))
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
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FC)) // Fondo general del proyecto
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Sección 1: Configuración de Correo
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0A3D91))
                        Spacer(Modifier.width(8.dp))
                        Text("Destinatario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2C3A2F))
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0A3D91),
                            focusedLabelColor = Color(0xFF0A3D91)
                        )
                    )
                }
            }

            // Sección 2: Enviar Ahora
            Button(
                onClick = viewModel::sendReportNow,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A3D91))
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar Reporte Ahora")
                }
            }

            Divider(color = Color(0xFFE0E0E0))

            // Sección 3: Programación
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF0A3D91))
                        Spacer(Modifier.width(8.dp))
                        Text("Programación Automática", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2C3A2F))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Activar envío programado")
                        Switch(
                            checked = state.isScheduled,
                            onCheckedChange = viewModel::onScheduleToggle
                        )
                    }

                    if (state.isScheduled) {
                        Spacer(Modifier.height(16.dp))

                        // Toggle Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilterChip(
                                selected = state.scheduleMode == ScheduleMode.INTERVAL,
                                onClick = { 
                                    viewModel.onScheduleModeChange(ScheduleMode.INTERVAL)
                                    viewModel.applySchedule()
                                },
                                label = { Text("Intervalo") },
                                leadingIcon = { if (state.scheduleMode == ScheduleMode.INTERVAL) Icon(Icons.Default.Schedule, null) else null }
                            )
                            Spacer(Modifier.width(16.dp))
                            FilterChip(
                                selected = state.scheduleMode == ScheduleMode.DAILY,
                                onClick = { 
                                    viewModel.onScheduleModeChange(ScheduleMode.DAILY)
                                    viewModel.applySchedule()
                                },
                                label = { Text("Diario") },
                                leadingIcon = { if (state.scheduleMode == ScheduleMode.DAILY) Icon(Icons.Default.Schedule, null) else null }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        if (state.scheduleMode == ScheduleMode.INTERVAL) {
                            Text("Intervalo (horas): ${state.intervalHours}")
                            Slider(
                                value = state.intervalHours.toFloat(),
                                onValueChange = { 
                                    viewModel.onIntervalChange(it.toLong())
                                    viewModel.applySchedule() // Apply on change end ideally, but here for simplicity
                                },
                                valueRange = 1f..48f,
                                steps = 47
                            )
                        } else {
                            // Simple Time Input for Daily Mode
                            Text("Hora del reporte (0-23) : (0-59)")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = state.dailyHour,
                                    onValueChange = { 
                                        viewModel.onDailyTimeChange(it, state.dailyMinute)
                                        // Optional: Apply schedule only if valid, or wait for user action? 
                                        // For now, let's try to apply if valid to give immediate feedback, or just let the user toggle off/on.
                                        // Better: Apply schedule if valid.
                                        if (it.toIntOrNull() != null) viewModel.applySchedule()
                                    },
                                    label = { Text("Hora") },
                                    modifier = Modifier.width(100.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0A3D91),
                                        focusedLabelColor = Color(0xFF0A3D91)
                                    )
                                )
                                Text(":", style = MaterialTheme.typography.titleLarge)
                                OutlinedTextField(
                                    value = state.dailyMinute,
                                    onValueChange = { 
                                        viewModel.onDailyTimeChange(state.dailyHour, it)
                                        if (it.toIntOrNull() != null) viewModel.applySchedule()
                                    },
                                    label = { Text("Minuto") },
                                    modifier = Modifier.width(100.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0A3D91),
                                        focusedLabelColor = Color(0xFF0A3D91)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
