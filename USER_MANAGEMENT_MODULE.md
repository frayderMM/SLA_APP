# Módulo de Gestión de Usuarios

## Descripción
Módulo para la aplicación móvil SLA que permite al administrador (analista@tcs.com) crear nuevos usuarios que tendrán acceso a la aplicación.

## Características Implementadas

### 1. **API Integration**
- **RegisterRequest.kt**: DTO para solicitudes de registro con campos: nombre, email, password, rolId
- **RegisterResponse.kt**: DTO para respuestas de registro
- **RolDto.kt**: DTO para información de roles
- **AuthApi.kt**: Actualizado con endpoints:
  - `POST /api/auth/register` - Registrar nuevo usuario
  - `GET /api/Roles` - Obtener lista de roles disponibles

### 2. **Repository Layer**
- **AuthRepository.kt**: Actualizado con métodos:
  - `register()` - Registra un nuevo usuario
  - `getRoles()` - Obtiene la lista de roles disponibles

### 3. **ViewModel & State Management**
- **UserManagementViewModel.kt**: Maneja la lógica de negocio
  - Carga de roles desde la API
  - Validación de formulario (nombre, email, contraseña, rol)
  - Registro de usuarios
  - Gestión de estados de carga y error

- **UserManagementState.kt**: Estados del módulo
  - `Idle` - Estado inicial
  - `Loading` - Cargando
  - `Success` - Registro exitoso
  - `Error` - Error en el proceso

- **UserFormState.kt**: Estado del formulario con validaciones

### 4. **UI Components**

#### AddUserScreen.kt
Pantalla principal del módulo con diseño coherente con el resto de la aplicación:

**Características:**
- Header con TopAppBar y botón de retroceso
- Formulario completo con validaciones en tiempo real:
  - Campo de nombre completo
  - Campo de correo electrónico (validación de formato)
  - Campo de contraseña (mínimo 6 caracteres, con toggle de visibilidad)
  - Campo de confirmación de contraseña (validación de coincidencia)
  - Selector de rol con RadioButtons
- Botón de registro con indicador de carga
- Botón de cancelar
- Mensajes Toast para feedback de éxito/error

**Diseño:**
- Colores coherentes con el tema de la app:
  - Primario: `#014A59`
  - Secundario: `#0084A8`
  - Fondo: `#F5F5F5`
  - Cards: Blanco con elevación
- Iconos temáticos (usuario, email, seguridad)
- Bordes redondeados y sombras suaves
- Scroll vertical para compatibilidad con diferentes tamaños de pantalla

### 5. **Navigation**

#### Routes.kt
Nuevas rutas agregadas:
- `USER_MANAGEMENT` - Ruta para gestión de usuarios
- `ADD_USER` - Ruta para agregar usuario

#### AppNavHost.kt
- Composable para `ADD_USER` con integración completa
- ViewModel factory configurado
- Navegación hacia atrás implementada

### 6. **Profile Integration**

#### ProfileScreen.kt
Modificaciones realizadas:
- Parámetro `onNavigateToUserManagement` agregado
- Validación de permisos: solo el usuario `analista@tcs.com` ve la opción
- Nueva opción "Gestionar Usuarios" en el menú de configuración
- Icono de usuario para la nueva opción

## Flujo de Usuario

1. **Inicio de sesión como administrador**
   - Email: `analista@tcs.com`
   - Password: `Analista123!`

2. **Acceso a gestión de usuarios**
   - Navegar a la pestaña "Perfil"
   - Seleccionar "Gestionar Usuarios" (visible solo para admin)

3. **Registro de nuevo usuario**
   - Completar formulario con información del usuario
   - Seleccionar el rol apropiado
   - Presionar "Registrar Usuario"
   - Confirmación mediante Toast

4. **Retorno**
   - Automático tras registro exitoso
   - Manual mediante botón "Cancelar" o botón de retroceso

## Validaciones Implementadas

### Nombre
- ✅ No puede estar vacío
- ✅ Mensaje de error en tiempo real

### Email
- ✅ No puede estar vacío
- ✅ Formato de email válido (validación con `Patterns.EMAIL_ADDRESS`)
- ✅ Mensaje de error descriptivo

### Contraseña
- ✅ No puede estar vacía
- ✅ Mínimo 6 caracteres
- ✅ Toggle para mostrar/ocultar
- ✅ Mensajes de error claros

### Confirmación de Contraseña
- ✅ No puede estar vacía
- ✅ Debe coincidir con la contraseña
- ✅ Validación visual inmediata
- ✅ Toggle independiente

### Rol
- ✅ Debe seleccionar un rol
- ✅ Carga dinámica desde la API
- ✅ Interfaz de selección intuitiva con RadioButtons

## Estructura de Archivos

```
SLA_APP/app/src/main/java/dev/esan/sla_app/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   └── AuthApi.kt (actualizado)
│   │   └── dto/
│   │       └── auth/
│   │           ├── RegisterRequest.kt (nuevo)
│   │           ├── RegisterResponse.kt (nuevo)
│   │           └── RolDto.kt (nuevo)
│   └── repository/
│       └── AuthRepository.kt (actualizado)
├── ui/
│   ├── navigation/
│   │   ├── Routes.kt (actualizado)
│   │   └── AppNavHost.kt (actualizado)
│   ├── profile/
│   │   └── ProfileScreen.kt (actualizado)
│   └── user_management/ (nuevo módulo)
│       ├── AddUserScreen.kt
│       ├── UserManagementState.kt
│       ├── UserManagementViewModel.kt
│       └── UserManagementViewModelFactory.kt
```

## Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Retrofit** - Cliente HTTP para consumo de API REST
- **ViewModel** - Gestión de estado y lógica de negocio
- **StateFlow** - Manejo reactivo de estados
- **Navigation Component** - Navegación entre pantallas
- **Material Design 3** - Componentes de UI

## API Endpoints Consumidos

### POST /api/Auth/register
Registra un nuevo usuario en el sistema.

**Request Body:**
```json
{
  "nombre": "string",
  "email": "string",
  "password": "string",
  "rolId": 0
}
```

**Response:**
```json
{
  "message": "string",
  "userId": 0
}
```

### GET /api/Roles
Obtiene la lista de roles disponibles.

**Response:**
```json
[
  {
    "id": 0,
    "nombre": "string"
  }
]
```

## Consideraciones de Seguridad

1. **Validación de permisos**: Solo el usuario administrador (`analista@tcs.com`) puede acceder al módulo
2. **Validación de entrada**: Todos los campos son validados antes de enviar a la API
3. **Contraseñas ocultas**: Por defecto, las contraseñas están ocultas con opción de mostrar
4. **Manejo de errores**: Mensajes claros al usuario sin exponer información sensible

## Mejoras Futuras Sugeridas

1. Implementar validación de fortaleza de contraseña (mayúsculas, números, caracteres especiales)
2. Agregar lista de usuarios registrados
3. Implementar funcionalidad de editar/eliminar usuarios
4. Agregar búsqueda y filtrado de usuarios
5. Implementar paginación para listas grandes
6. Agregar confirmación antes de crear usuario
7. Implementar generación automática de contraseñas seguras

## Autor
Módulo desarrollado para el proyecto SLA App - Control y Seguimiento de Indicadores SLA

## Fecha
Diciembre 2025
