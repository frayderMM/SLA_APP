package dev.esan.sla_app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esan.sla_app.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // ⭐ Obtenemos los colores dinámicos del tema
    val c = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)     // ⭐ ahora sí aplica el tema
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Image(
            painter = painterResource(id = R.drawable.logotata),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(300f / 121f)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // ⭐ TARJETA con borde dinámico del tema
        Column(
            modifier = Modifier
                .width(350.dp)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            c.primary.copy(alpha = 0.50f),
                            c.primary.copy(alpha = 0.18f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {

            // ----------------------------------
            // EMAIL
            // ----------------------------------
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        "Correo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = c.primary
                    )
                },
                enabled = !state.loading,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = c.onSurface      // ⭐ se adapta al tema
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = c.primary,
                    unfocusedBorderColor = c.outline,
                    cursorColor = c.primary,
                    focusedTextColor = c.onSurface,
                    unfocusedTextColor = c.onSurface,
                    focusedLabelColor = c.primary,
                    unfocusedLabelColor = c.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(12.dp))

            // ----------------------------------
            // PASSWORD
            // ----------------------------------
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Contraseña",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = c.primary
                    )
                },
                enabled = !state.loading,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = c.onSurface        // ⭐ dinámico
                ),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = c.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = c.primary,
                    unfocusedBorderColor = c.outline,
                    cursorColor = c.primary,
                    focusedTextColor = c.onSurface,
                    unfocusedTextColor = c.onSurface,
                    focusedLabelColor = c.primary,
                    unfocusedLabelColor = c.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ----------------------------------
        // BOTÓN PRINCIPAL
        // ----------------------------------
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = !state.loading,
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = c.primary,     // ⭐ dinámico
                contentColor = c.onPrimary      // ⭐ dinámico
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "INICIAR SESIÓN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ----------------------------------
        // LOADING
        // ----------------------------------
        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = c.primary)
        }

        // ----------------------------------
        // ERROR
        // ----------------------------------
        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                color = c.error,     // ⭐ dinámico para dark/light
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
