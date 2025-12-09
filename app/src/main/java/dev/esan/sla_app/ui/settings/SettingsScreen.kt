package dev.esan.sla_app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.esan.sla_app.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // =====================
            //  TÍTULO PRINCIPAL
            // =====================
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            // =====================
            //  SECCIÓN: TEMAS
            // =====================
            SettingsSection(title = "Tema (Apariencia)") {

                ThemeOptionItem(
                    name = "Azul fuerte",
                    color = blue_primary,
                    selected = state.themeIndex == 0,
                    onClick = { viewModel.onThemeChanged(0) }
                )

                ThemeOptionItem(
                    name = "Azul celeste (oscuro)",
                    color = blueDark_primary,
                    selected = state.themeIndex == 1,
                    onClick = { viewModel.onThemeChanged(1) }
                )

                ThemeOptionItem(
                    name = "Verde profesional",
                    color = green_primary,
                    selected = state.themeIndex == 2,
                    onClick = { viewModel.onThemeChanged(2) }
                )

                ThemeOptionItem(
                    name = "Coral premium",
                    color = coral_primary,
                    selected = state.themeIndex == 3,
                    onClick = { viewModel.onThemeChanged(3) }
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // =====================
            //  SECCIÓN: IDIOMA
            // =====================
            SettingsSection(title = "Idioma") {
                RadioOptionItem(
                    text = "Español",
                    selected = state.language == "es",
                    onClick = { viewModel.onLanguageChange("es") }
                )
                RadioOptionItem(
                    text = "English",
                    selected = state.language == "en",
                    onClick = { viewModel.onLanguageChange("en") }
                )
            }
        }
    }
}


@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ThemeOptionItem(
    name: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Esfera de color representativa del tema
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        RadioButton(selected = selected, onClick = { onClick() })
    }
}

@Composable
fun RadioOptionItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
