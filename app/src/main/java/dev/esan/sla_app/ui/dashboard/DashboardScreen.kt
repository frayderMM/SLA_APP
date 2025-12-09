package dev.esan.sla_app.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults // Este import es vital
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- IMPORTS DE VICO 1.16.0 (Limpios de errores) ---
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
// ----------------------------------------------------
import dev.esan.sla_app.data.remote.dto.insight.InsightHistoricoDto
import dev.esan.sla_app.data.remote.dto.insight.InsightIndicadoresDto
import dev.esan.sla_app.ui.insight.InsightPanelScreen
import dev.esan.sla_app.ui.insight.InsightPanelViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InsightPanelViewModel,
    onNavigateToAlerts: () -> Unit,
    onNavigateToRegression: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedSla by remember { mutableStateOf("SLA1") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedSla) {
        viewModel.load(selectedSla)
    }

    Scaffold(
        topBar = {
            DashboardTopAppBar(onNavigateToAlerts, onNavigateToProfile)
        },
        containerColor = Color(0xFFF0F4F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Header(title = "Dashboard de SLAs")

            SlaSelector(
                selected = selectedSla,
                onSelect = { selectedSla = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            InsightPanelScreen(viewModel)

            Spacer(Modifier.height(16.dp))

            ChartsCard(state, selectedSla)

            Spacer(Modifier.height(16.dp))

            RegressionButton(onNavigateToRegression)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopAppBar(onNavigateToAlerts: () -> Unit, onNavigateToProfile: () -> Unit) {
    TopAppBar(
        title = { Text("SLA Dashboard", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.White) },
        actions = {
            IconButton(onClick = onNavigateToAlerts) {
                Icon(Icons.Default.Notifications, contentDescription = "Alertas", tint = Color.White)
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(Color(0xFF0A3D91), Color(0xFF0055A4))
            )
        )
    )
}

@Composable
fun Header(title: String) {
    Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0A3D91),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ChartsCard(state: dev.esan.sla_app.ui.insight.InsightPanelState, selectedSla: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SlaDistributionChart()
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                "N° de Solicitudes por Mes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.loading && (state.historico == null || state.indicadores == null)) {
                    CircularProgressIndicator(color = Color(0xFF0A3D91))
                } else if (state.error != null) {
                    Text(
                        "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                } else if (state.historico != null && state.indicadores != null && state.historico!!.historico.isNotEmpty()) {
                    FrequencyChart(
                        indicadores = state.indicadores!!,
                        historico = state.historico!!,
                        selectedSla = selectedSla
                    )
                } else {
                    Text("No hay datos históricos para mostrar.")
                }
            }
        }
    }
}

@Composable
fun RegressionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0055A4)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text("Ver Regresión Lineal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun SlaDistributionChart() {
    val data = mapOf("SLA1" to 65f, "SLA2" to 35f)
    val colors = listOf(Color(0xFF007BFF), Color(0xFF28A745))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Distribución Total de SLAs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(150.dp)) {
                val total = data.values.sum()
                var startAngle = -90f
                data.entries.forEachIndexed { index, entry ->
                    val sweep = (entry.value / total) * 360f
                    val animatedSweep by animateFloatAsState(targetValue = sweep, label = "")
                    Canvas(Modifier.fillMaxSize()) {
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            style = Stroke(30f, cap = StrokeCap.Butt)
                        )
                    }
                    startAngle += sweep
                }
            }
            Spacer(Modifier.width(32.dp))
            Column {
                data.entries.forEachIndexed { index, entry ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Box(Modifier.size(16.dp).background(colors[index], CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text("${entry.key}: ${entry.value}%", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FrequencyChart(indicadores: InsightIndicadoresDto, historico: InsightHistoricoDto, selectedSla: String) {
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var monthLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(historico, selectedSla) {
        val monthData = mutableMapOf<String, Int>()
        val cal = Calendar.getInstance()
        val monthKeyFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        for (i in 0 until 12) {
            cal.add(Calendar.MONTH, -1)
            monthData[monthKeyFormat.format(cal.time)] = 0
        }

        val weightsPerMonth = mutableMapOf<String, Double>()
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
                    weightsPerMonth[key] = (weightsPerMonth[key] ?: 0.0) + item.porcentaje
                }
            }
        }

        val totalWeight = weightsPerMonth.values.sum()
        val totalRequests = indicadores.total

        if (totalWeight > 0) {
            weightsPerMonth.forEach { (key, weight) ->
                val proportionalRequests = (weight / totalWeight) * totalRequests
                monthData[key] = proportionalRequests.roundToInt()
            }

            val currentSum = monthData.values.sum()
            var difference = totalRequests - currentSum
            val sortedMonths = monthData.entries.sortedByDescending { it.value }.map { it.key }
            var i = 0
            while (difference != 0 && sortedMonths.isNotEmpty()) {
                val keyToAdjust = sortedMonths[i % sortedMonths.size]
                if (difference > 0) {
                    monthData[keyToAdjust] = monthData[keyToAdjust]!! + 1
                    difference--
                } else {
                    if (monthData[keyToAdjust]!! > 0) {
                        monthData[keyToAdjust] = monthData[keyToAdjust]!! - 1
                        difference++
                    }
                }
                i++
                if (i > totalRequests * 2) break
            }
        }

        val sortedKeys = monthData.keys.sorted()
        val entries = mutableListOf<com.patrykandpatrick.vico.core.entry.ChartEntry>()
        val labels = mutableListOf<String>()
        val labelFormat = SimpleDateFormat("MMM", Locale.US)
        val keyParser = SimpleDateFormat("yyyy-MM", Locale.US)

        sortedKeys.forEachIndexed { index, key ->
            entries.add(entryOf(index.toFloat(), monthData[key] ?: 0))
            keyParser.parse(key)?.let { labels.add(labelFormat.format(it)) } ?: labels.add("")
        }

        chartEntryModelProducer.setEntries(entries)
        monthLabels = labels
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        monthLabels.getOrElse(value.toInt()) { "" }
    }

    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        value.toInt().toString()
    }

    Chart(
        chart = columnChart(
            columns = listOf(
                LineComponent(
                    color = Color(0xFF007BFF).toArgb(),
                    thicknessDp = 20f,
                    // 🔥 SOLUCIÓN DEFINITIVA: Usamos pillShape.
                    // Esto evita el error de "Corner" porque no usamos esa clase.
                    shape = Shapes.pillShape,
                    // shader se llama dynamicShader en la 1.16.0
                    dynamicShader = DynamicShaders.fromBrush(
                        Brush.verticalGradient(
                            listOf(Color(0xFF007BFF), Color(0xFF0A3D91))
                        )
                    )
                )
            )
        ),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(valueFormatter = startAxisValueFormatter),
        bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlaSelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("SLA Seleccionado") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0A3D91),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF0A3D91)
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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