package dev.esan.sla_app.ui.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.esan.sla_app.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    var showChangePassword by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }

    // Observar el resultado para detener la carga y mostrar feedback
    LaunchedEffect(viewModel) {
        viewModel.changePasswordResult.collect {
            isChangingPassword = false
            // Aquí se podría mostrar un Snackbar en lugar de un Toast
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguridad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            // --- Opciones de Seguridad ---
            SecurityOptionItem(
                title = "Cambiar Contraseña",
                description = "Se recomienda usar una contraseña segura que no uses en otros sitios.",
                onClick = { showChangePassword = !showChangePassword }
            )

            // --- Formulario para Cambiar Contraseña (Aparece con animación) ---
            AnimatedVisibility(visible = showChangePassword) {
                ChangePasswordForm(
                    isLoading = isChangingPassword,
                    onChangePassword = {
                        currentPassword, newPassword ->
                        isChangingPassword = true
                        viewModel.changePassword(currentPassword, newPassword)
                    }
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Aquí se podrían añadir más opciones de seguridad en el futuro
            // SecurityOptionItem(title = "Autenticación de dos factores", description = "Añade una capa extra de seguridad", onClick = {})
        }
    }
}

@Composable
private fun SecurityOptionItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
private fun ChangePasswordForm(
    isLoading: Boolean,
    onChangePassword: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) } // <-- AÑADIDO para el bug

    val requirements by remember(newPassword) {
        derivedStateOf {
            PasswordRequirements(
                hasMinLength = newPassword.length >= 8,
                hasUppercase = newPassword.any { it.isUpperCase() },
                hasNumber = newPassword.any { it.isDigit() },
                hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }
            )
        }
    }

    val allRequirementsMet = requirements.run { hasMinLength && hasUppercase && hasNumber && hasSpecialChar }
    val passwordsMatch = newPassword == confirmPassword && newPassword.isNotEmpty()
    val isFormValid = allRequirementsMet && passwordsMatch && currentPassword.isNotEmpty()

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PasswordTextField(
                password = currentPassword,
                onPasswordChange = { currentPassword = it },
                label = "Contraseña Actual",
                isVisible = currentPasswordVisible,
                onVisibilityChange = { currentPasswordVisible = !currentPasswordVisible }
            )
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                password = newPassword,
                onPasswordChange = { newPassword = it },
                label = "Nueva Contraseña",
                isVisible = newPasswordVisible,
                onVisibilityChange = { newPasswordVisible = !newPasswordVisible }
            )
            Spacer(Modifier.height(12.dp))
            PasswordStrengthIndicator(newPassword, requirements) // <-- CORREGIDO
            Spacer(Modifier.height(16.dp))
            PasswordRequirementsList(requirements)
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it },
                label = "Confirmar Nueva Contraseña",
                isVisible = confirmPasswordVisible, // <-- CORREGIDO
                onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible }, // <-- CORREGIDO
                isError = confirmPassword.isNotEmpty() && !passwordsMatch
            )
            if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onChangePassword(currentPassword, newPassword) },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Actualizar Contraseña")
                }
            }
        }
    }
}

// --- Componentes auxiliares de la versión anterior (reutilizados y mejorados) ---

private data class PasswordRequirements(
    val hasMinLength: Boolean, val hasUppercase: Boolean, val hasNumber: Boolean, val hasSpecialChar: Boolean
)

@Composable
private fun PasswordTextField(
    password: String, onPasswordChange: (String) -> Unit, label: String, isVisible: Boolean, onVisibilityChange: () -> Unit, isError: Boolean = false
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onVisibilityChange) { Icon(image, contentDescription = null) }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordStrengthIndicator(newPassword: String, requirements: PasswordRequirements) { // <-- CORREGIDO
    val strengthScore = listOf(requirements.hasMinLength, requirements.hasUppercase, requirements.hasNumber, requirements.hasSpecialChar).count { it }
    val progress = strengthScore / 4f
    val (color, text) = when (strengthScore) {
        0, 1 -> Color(0xFFD32F2F) to "Débil"
        2, 3 -> Color(0xFFF57C00) to "Media"
        4 -> Color(0xFF388E3C) to "Fuerte"
        else -> Color.Gray to ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.5f),
            strokeCap = StrokeCap.Round
        )
        if (newPassword.isNotEmpty()) { // <-- CORREGIDO
            Text(text, color = color, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
        }
    }
}

@Composable
private fun PasswordRequirementsList(requirements: PasswordRequirements) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
        RequirementRow("Mínimo 8 caracteres", requirements.hasMinLength)
        RequirementRow("Al menos una mayúscula", requirements.hasUppercase)
        RequirementRow("Al menos un número", requirements.hasNumber)
        RequirementRow("Al menos un carácter especial", requirements.hasSpecialChar)
    }
}

@Composable
private fun RequirementRow(text: String, isMet: Boolean) {
    val color = if (isMet) Color(0xFF388E3C) else Color.Gray
    val icon = Icons.Filled.Check

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = color, style = MaterialTheme.typography.bodyMedium)
    }
}
