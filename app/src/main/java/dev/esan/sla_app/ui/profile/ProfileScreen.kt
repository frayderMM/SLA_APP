package dev.esan.sla_app.ui.profile

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esan.sla_app.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.changePasswordResult.collectLatest {
            it.fold(
                onSuccess = { Toast.makeText(context, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show() },
                onFailure = { Toast.makeText(context, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    val user by viewModel.userProfileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logoutState.collectLatest { onLogout() }
    }

    if (user == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentUser = user!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderOcean(name = currentUser.nombre ?: "Usuario")
        Spacer(modifier = Modifier.height(60.dp))
        CardPersonalOcean(
            nombre = currentUser.nombre ?: "No disponible",
            email = currentUser.email,
            rol = currentUser.role
        )
        Spacer(modifier = Modifier.height(26.dp))
        CardMenuOcean(
            onSecurityClick = onNavigateToSecurity,
            onSettingsClick = onNavigateToSettings
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            LogoutButtonOcean(onClick = { viewModel.onLogoutClicked() })
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileHeaderOcean(name: String) {
    Box(modifier = Modifier.fillMaxWidth().height(310.dp)) {
        Image(painter = painterResource(R.drawable.fondo), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Canvas(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(170.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.70f)
                cubicTo(size.width * 0.23f, size.height * 0.70f, size.width * 0.30f, size.height * 0.20f, size.width * 0.50f, size.height * 0.20f)
                cubicTo(size.width * 0.70f, size.height * 0.20f, size.width * 0.76f, size.height * 0.70f, size.width, size.height * 0.70f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color = Color.White, style = Fill)
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.alexa), contentDescription = null, modifier = Modifier.size(150.dp).clip(CircleShape).border(5.dp, Color.White, CircleShape).shadow(12.dp), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color(0xFF014A59))
        }
    }
}

@Composable
fun CardPersonalOcean(nombre: String, email: String, rol: String) {
    Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFFEBF9FF)), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Información Personal", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color(0xFF014A59))
            Spacer(modifier = Modifier.height(12.dp))
            InfoRowOcean(icon = R.drawable.ic_user, label = "Nombre", value = nombre)
            Divider()
            InfoRowOcean(icon = R.drawable.ic_email, label = "Correo", value = email)
            Divider()
            InfoRowOcean(icon = R.drawable.ic_shield, label = "Rol", value = rol)
        }
    }
}

@Composable
fun InfoRowOcean(icon: Int, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = Color(0xFF0084A8), modifier = Modifier.size(23.dp))
        Spacer(modifier = Modifier.width(18.dp))
        Column {
            Text(label, fontSize = 13.sp, color = Color(0xFF5A6168))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF014A59))
        }
    }
}

@Composable
fun CardMenuOcean(onSecurityClick: () -> Unit, onSettingsClick: () -> Unit) {
    Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFFEBF9FF)), elevation = CardDefaults.cardElevation(8.dp), shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(6.dp)) {
            MenuItemOcean(R.drawable.ic_notifications, "Notificaciones", onClick = {})
            Divider()
            MenuItemOcean(R.drawable.ic_settings, "Configuración", onClick = onSettingsClick)
            Divider()
            MenuItemOcean(R.drawable.ic_help, "Centro de ayuda", onClick = {})
            Divider()
            MenuItemOcean(R.drawable.ic_security, "Seguridad", onClick = onSecurityClick)
        }
    }
}

@Composable
fun MenuItemOcean(icon: Int, title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(56.dp).clickable(onClick = onClick).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = Color(0xFF00AACC), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(18.dp))
        Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Color(0xFF014A59), modifier = Modifier.weight(1f))
        Icon(painter = painterResource(R.drawable.ic_arrow_forward), contentDescription = null, tint = Color(0xFF0084A8), modifier = Modifier.size(19.dp))
    }
}

@Composable
fun LogoutButtonOcean(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.width(240.dp).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF4646)), shape = RoundedCornerShape(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(R.drawable.ic_logout), contentDescription = null, tint = Color.Red, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Cerrar sesión", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Red)
        }
    }
}
