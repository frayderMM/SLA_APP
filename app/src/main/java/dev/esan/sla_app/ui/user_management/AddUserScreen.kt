package dev.esan.sla_app.ui.user_management

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esan.sla_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    viewModel: UserManagementViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Observar estados de éxito y error
    LaunchedEffect(state) {
        when (state) {
            is UserManagementState.Success -> {
                Toast.makeText(
                    context,
                    (state as UserManagementState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetState()
                onNavigateBack()
            }
            is UserManagementState.Error -> {
                Toast.makeText(
                    context,
                    (state as UserManagementState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Usuario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF014A59)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Título de sección
            Text(
                text = "Complete la información del nuevo usuario",
                fontSize = 16.sp,
                color = Color(0xFF5A6168),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Card principal del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Campo Nombre
                    OutlinedTextField(
                        value = formState.nombre,
                        onValueChange = { viewModel.updateNombre(it) },
                        label = { Text("Nombre Completo") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = null,
                                tint = Color(0xFF0084A8)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.nombreError != null,
                        supportingText = {
                            formState.nombreError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0084A8),
                            focusedLabelColor = Color(0xFF0084A8)
                        )
                    )

                    // Campo Email
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_email),
                                contentDescription = null,
                                tint = Color(0xFF0084A8)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.emailError != null,
                        supportingText = {
                            formState.emailError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0084A8),
                            focusedLabelColor = Color(0xFF0084A8)
                        )
                    )

                    // Campo Contraseña
                    OutlinedTextField(
                        value = formState.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_security),
                                contentDescription = null,
                                tint = Color(0xFF0084A8)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) 
                                        Icons.Filled.Visibility 
                                    else 
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) 
                                        "Ocultar contraseña" 
                                    else 
                                        "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.passwordError != null,
                        supportingText = {
                            formState.passwordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0084A8),
                            focusedLabelColor = Color(0xFF0084A8)
                        )
                    )

                    // Campo Confirmar Contraseña
                    OutlinedTextField(
                        value = formState.confirmPassword,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        label = { Text("Confirmar Contraseña") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_security),
                                contentDescription = null,
                                tint = Color(0xFF0084A8)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible)
                                        "Ocultar contraseña"
                                    else
                                        "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.confirmPasswordError != null,
                        supportingText = {
                            formState.confirmPasswordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0084A8),
                            focusedLabelColor = Color(0xFF0084A8)
                        )
                    )

                    // Selector de Rol
                    Column {
                        Text(
                            text = "Rol del Usuario",
                            fontSize = 14.sp,
                            color = Color(0xFF5A6168),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (formState.isLoadingRoles) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF0084A8)
                                )
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(Color(0xFFEBF9FF)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    formState.roles.forEach { rol ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = formState.rolId == rol.id,
                                                onClick = { viewModel.updateRol(rol.id) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = Color(0xFF0084A8)
                                                )
                                            )
                                            Text(
                                                text = rol.nombre,
                                                fontSize = 16.sp,
                                                color = Color(0xFF014A59),
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        formState.rolError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Registrar
            Button(
                onClick = { viewModel.registerUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0084A8)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = state !is UserManagementState.Loading
            ) {
                if (state is UserManagementState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_user),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Registrar Usuario",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Botón de Cancelar
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF014A59)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
