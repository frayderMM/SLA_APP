package dev.esan.sla_app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
                .verticalScroll(scrollState), // Habilita scroll si la pantalla es pequeña
            verticalArrangement = Arrangement.spacedBy(24.dp) // Más espacio entre secciones
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsSection(title = "Tema") {
                RadioOptionItem(
                    text = "Sistema",
                    selected = state.theme == "system",
                    onClick = { viewModel.onThemeChange("system") }
                )
                RadioOptionItem(
                    text = "Claro",
                    selected = state.theme == "light",
                    onClick = { viewModel.onThemeChange("light") }
                )
                RadioOptionItem(
                    text = "Oscuro",
                    selected = state.theme == "dark",
                    onClick = { viewModel.onThemeChange("dark") }
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant) // Separador visual

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
            modifier = Modifier.padding(start = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
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
            style = MaterialTheme.typography.bodyLarge
        )
    }
}