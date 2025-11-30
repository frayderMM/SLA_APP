package dev.esan.sla_app.ui.profile

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
    modifier: Modifier = Modifier
) {
    // 🔥 1. OBSERVAR EL ESTADO CORRECTO DEL VIEWMODEL
    val user by viewModel.userProfileState.collectAsState()

    // Lanzar el efecto de logout solo una vez
    LaunchedEffect(Unit) {
        viewModel.logoutState.collectLatest {
            onLogout()
        }
    }

    // 🔥 2. LA LÓGICA DE CARGA AHORA DEPENDE DE SI EL USUARIO ES NULO
    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // A partir de aquí, 'user' no es nulo
    val currentUser = user!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // HEADER
        ProfileHeaderOcean(
            name = currentUser.nombre ?: "Usuario"
        )

        Spacer(modifier = Modifier.height(60.dp))

        // CARD DE DATOS PERSONALES
        CardPersonalOcean(
            // 🔥 3. PASAR LAS PROPIEDADES DEL NUEVO MODELO
            nombre = currentUser.nombre ?: "No disponible",
            email = currentUser.email,
            rol = currentUser.role // La propiedad 'role' se mapea al parámetro 'rol'
        )

        Spacer(modifier = Modifier.height(26.dp))

        // MENÚ DE OPCIONES
        CardMenuOcean()

        Spacer(modifier = Modifier.height(30.dp))

        // BOTÓN CERRAR SESIÓN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LogoutButtonOcean(
                onClick = {
                    // 🔥 4. LLAMAR A LA FUNCIÓN CORRECTA EN EL VIEWMODEL
                    viewModel.onLogoutClicked()
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

//
// ===================================================================
// ⭐ HEADER OCEAN (curva exacta + fondo exacto + ALEXA)
// ===================================================================
//
@Composable
fun ProfileHeaderOcean(name: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
    ) {

        // Fondo (tiene que existir en drawable)
        Image(
            painter = painterResource(R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Curva exacta
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(170.dp)
        ) {
            val w = size.width
            val h = size.height

            val path = Path().apply {
                moveTo(0f, h * 0.70f)
                cubicTo(
                    w * 0.23f, h * 0.70f,
                    w * 0.30f, h * 0.20f,
                    w * 0.50f, h * 0.20f
                )
                cubicTo(
                    w * 0.70f, h * 0.20f,
                    w * 0.76f, h * 0.70f,
                    w, h * 0.70f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(path, color = Color.White, style = Fill)
        }

        // Avatar + Nombre
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ⭐ alexa (imagen circular)
            Image(
                painter = painterResource(R.drawable.alexa),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(5.dp, Color.White, CircleShape)
                    .shadow(12.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF014A59)
            )
        }
    }
}

//
// ===================================================================
// ⭐ CARD CON INFORMACIÓN PERSONAL (solo datos que EXISTEN en Código 1)
// ===================================================================
//
@Composable
fun CardPersonalOcean(nombre: String, email: String, rol: String) {

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFFEBF9FF)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                "Información Personal",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF014A59)
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRowOcean(
                icon = R.drawable.ic_user,
                label = "Nombre",
                value = nombre
            )
            Divider()

            InfoRowOcean(
                icon = R.drawable.ic_email,
                label = "Correo",
                value = email
            )
            Divider()

            InfoRowOcean(
                icon = R.drawable.ic_shield,
                label = "Rol",
                value = rol
            )
        }
    }
}

@Composable
fun InfoRowOcean(icon: Int, label: String, value: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color(0xFF0084A8),
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Column {
            Text(label, fontSize = 13.sp, color = Color(0xFF5A6168))
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF014A59)
            )
        }
    }
}

//
// ===================================================================
// ⭐ MENÚ OCEAN (SIN NAVEGABILIDAD)
// ===================================================================
//
@Composable
fun CardMenuOcean() {

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFFEBF9FF)),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(modifier = Modifier.padding(6.dp)) {

            MenuItemOcean(R.drawable.ic_notifications, "Notificaciones")
            Divider()

            MenuItemOcean(R.drawable.ic_settings, "Configuración")
            Divider()

            MenuItemOcean(R.drawable.ic_help, "Centro de ayuda")
            Divider()

            MenuItemOcean(R.drawable.ic_security, "Seguridad")
        }
    }
}

@Composable
fun MenuItemOcean(icon: Int, title: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { /* sin navegación */ }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color(0xFF00AACC),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF014A59),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = Color(0xFF0084A8),
            modifier = Modifier.size(19.dp)
        )
    }
}

//
// ===================================================================
// ⭐ BOTÓN CERRAR SESIÓN
// ===================================================================
//
@Composable
fun LogoutButtonOcean(onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x33FF4646)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Cerrar sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
