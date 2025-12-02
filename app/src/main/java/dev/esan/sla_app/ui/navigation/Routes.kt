package dev.esan.sla_app.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val REGRESSION = "regression"
    const val INDICADORES = "indicadores"
    const val ALERTAS = "alertas"
    const val PDF = "pdf"
    const val PROFILE = "profile"

    const val DASHBOARD_GRAPH = "dashboard_graph"

    // 🔥 Agrupamos las rutas de solicitudes bajo un grafo para compartir el ViewModel
    const val SOLICITUDES_GRAPH = "solicitudes_graph" // Ruta del grafo
    const val SOLICITUDES_LIST = "solicitudes_list"  // Ruta para la pantalla de la lista
    const val SOLICITUD_CREAR = "solicitudes_crear"
    const val SOLICITUD_EDITAR = "solicitudes_editar/{id}"
    const val EMAIL_CONFIG = "email_config"
}
