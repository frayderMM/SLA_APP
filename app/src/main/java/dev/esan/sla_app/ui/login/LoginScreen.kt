package dev.esan.sla_app.ui.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ✔ Mantengo tu navegación EXACTA
    LaunchedEffect(state.success) {
        if (state.success) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        // ⭐ Inserto el LOGO del diseño 2
        Image(
            painter = painterResource(id = R.drawable.logotata),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(300f / 121f)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // ⭐ TARJETA con borde y shape del diseño 2
        Column(
            modifier = Modifier
                .width(350.dp)
                .border(3.dp, Color(0x2AFF9969), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {

            // ⭐ EMAIL (ANTES username)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        "Usuario / Correo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                enabled = !state.loading,
                textStyle = TextStyle(fontSize = 20.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // ⭐ CONTRASEÑA con ícono visible / oculto del diseño 2
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Contraseña",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                enabled = !state.loading,
                textStyle = TextStyle(fontSize = 20.sp),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ⭐ Botón estilizado del diseño 2
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = !state.loading,
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3280C4)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "INICIAR SESIÓN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ⭐ Indicadores del estado original (loading, error)
        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
