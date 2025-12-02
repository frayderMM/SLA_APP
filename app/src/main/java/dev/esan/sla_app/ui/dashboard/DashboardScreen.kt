package dev.esan.sla_app.ui.dashboard

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dev.esan.sla_app.data.remote.dto.insight.InsightHistoricoDto
import dev.esan.sla_app.ui.insight.InsightPanelScreen
import dev.esan.sla_app.ui.insight.InsightPanelViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InsightPanelViewModel,
    onNavigateToAlerts: () -> Unit,
    onNavigateToRegression: () -> Unit
) {
    var selectedSla by remember { mutableStateOf("SLA1") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedSla) {
        viewModel.load(selectedSla)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard SLA") },
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

            SlaSelector(
                selected = selectedSla,
                onSelect = { selectedSla = it }
            )

            Spacer(Modifier.height(16.dp))

            InsightPanelScreen(viewModel)

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Solicitudes por mes",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            state.loading && state.historico == null -> {
                                CircularProgressIndicator()
                            }
                            state.error != null -> {
                                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                            }
                            state.historico != null && state.historico!!.historico.isNotEmpty() -> {
                                FrequencyChart(historico = state.historico!!, selectedSla = selectedSla)
                            }
                            else -> {
                                Text("No hay datos históricos disponibles.")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SlaDistributionChart()

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onNavigateToRegression,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Ver Regresión Lineal")
            }
        }
    }
}

@Composable
fun SlaDistributionChart() {
    // Datos estáticos para el gráfico circular
    val data = mapOf(
        "SLA1" to 65f,
        "SLA2" to 35f
    )
    val colors = listOf(Color(0xFF007BFF), Color(0xFF28A745), Color(0xFFFFC107), Color(0xFFDC3545))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Distribución Total de SLAs",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gráfico Circular
                Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val total = data.values.sum()
                        var startAngle = -90f
                        data.values.forEachIndexed { index, value ->
                            val sweepAngle = (value / total) * 360f
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 35f, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                Spacer(Modifier.width(24.dp))

                // Leyenda
                Column {
                    data.keys.forEachIndexed { index, label ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(colors[index % colors.size])
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("$label: ${data[label]}%", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FrequencyChart(historico: InsightHistoricoDto, selectedSla: String) {
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var monthLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(historico, selectedSla) {
        val monthData = mutableMapOf<String, Double>()
        val cal = Calendar.getInstance()
        val monthKeyFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        for (i in 0 until 12) {
            monthData[monthKeyFormat.format(cal.time)] = 0.0
            cal.add(Calendar.MONTH, -1)
        }

        historico.historico.forEach { item ->
            val itemPeriodo = item.periodo
            val date: Date? = itemPeriodo.toDoubleOrNull()?.toLong()?.let {
                if (it.toString().length == 10) Date(it * 1000) else Date(it)
            } ?: try {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(itemPeriodo)
            } catch (e: ParseException) {
                try {
                    SimpleDateFormat("yyyy-MM", Locale.US).parse(itemPeriodo)
                } catch (e2: ParseException) {
                    null
                }
            }

            date?.let {
                val key = monthKeyFormat.format(it)
                if (monthData.containsKey(key)) {
                    monthData[key] = item.porcentaje
                }
            }
        }

        val sortedKeys = monthData.keys.sorted()
        val entries = mutableListOf<com.patrykandpatrick.vico.core.entry.ChartEntry>()
        val labels = mutableListOf<String>()
        val labelFormat = SimpleDateFormat("MMM", Locale.US)
        val keyParser = SimpleDateFormat("yyyy-MM", Locale.US)

        sortedKeys.forEachIndexed { index, key ->
            entries.add(entryOf(index.toFloat(), monthData[key] ?: 0.0))
            keyParser.parse(key)?.let {
                labels.add(labelFormat.format(it))
            } ?: labels.add("")
        }

        chartEntryModelProducer.setEntries(entries)
        monthLabels = labels
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        monthLabels.getOrElse(value.toInt()) { "" }
    }

    Chart(
        modifier = Modifier.fillMaxSize(),
        chart = columnChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
    )
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
