package dev.esan.sla_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.di.AppContainer
import dev.esan.sla_app.di.DefaultAppContainer
import dev.esan.sla_app.ui.alertas.*
import dev.esan.sla_app.ui.dashboard.*
import dev.esan.sla_app.ui.insight.InsightPanelViewModel
import dev.esan.sla_app.ui.insight.InsightPanelViewModelFactory
import dev.esan.sla_app.ui.login.*
import dev.esan.sla_app.ui.pdf.PdfViewModel
import dev.esan.sla_app.ui.pdf.PdfViewModelFactory
import dev.esan.sla_app.ui.profile.*
import dev.esan.sla_app.ui.sla.*
import dev.esan.sla_app.ui.solicitudes.*
import kotlinx.coroutines.flow.first

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val context = LocalContext.current
    val appContainer: AppContainer = remember(context) {
        DefaultAppContainer(context)
    }

    LaunchedEffect(Unit) {
        val token = appContainer.dataStoreManager.token.first()
        val startDestination = if (token != null) {
            RetrofitClient.authInterceptor.setToken(token)
            Routes.DASHBOARD
        } else {
            Routes.LOGIN
        }
        navController.navigate(startDestination) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        composable(Routes.LOGIN) {
            val loginVM: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(appContainer.authRepository, appContainer.dataStoreManager)
            )
            LoginScreen(viewModel = loginVM) {
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }

        composable(Routes.DASHBOARD) {
            val insightVM: InsightPanelViewModel = viewModel(
                factory = InsightPanelViewModelFactory(appContainer.insightRepository)
            )
            MainScreen(navController = navController) {
                DashboardScreen(
                    viewModel = insightVM,
                    onNavigateToSolicitudes = { navController.navigate(Routes.INDICADORES) },
                    onNavigateToAlerts = { navController.navigate(Routes.ALERTAS) }
                )
            }
        }

        composable(Routes.INDICADORES) {
            val indicadoresVM: IndicadoresViewModel = viewModel(
                factory = IndicadoresViewModelFactory(appContainer.slaRepository)
            )
            val pdfVM: PdfViewModel = viewModel(
                factory = PdfViewModelFactory(appContainer.reportesRepository)
            )
            MainScreen(navController = navController) {
                IndicadoresScreen(
                    indicadoresViewModel = indicadoresVM,
                    pdfViewModel = pdfVM,
                    onNavigateToSolicitudes = { navController.navigate(Routes.SOLICITUDES_GRAPH) }
                )
            }
        }

        // 🔥 Llama a la función que define el grafo de solicitudes anidado
        solicitudesGraph(navController, appContainer)

        composable(Routes.ALERTAS) {
            val alertasVM: AlertasViewModel = viewModel(
                factory = AlertasViewModelFactory(appContainer.alertasRepository)
            )
            MainScreen(navController = navController) {
                AlertasScreen(
                    viewModel = alertasVM,
                    onNavigateToEmailConfig = { navController.navigate(Routes.EMAIL_CONFIG) }
                )
            }
        }

        composable(Routes.EMAIL_CONFIG) {
            val emailVM: dev.esan.sla_app.ui.email.EmailConfigViewModel = viewModel(
                factory = dev.esan.sla_app.ui.email.EmailConfigViewModelFactory(appContainer.emailRepository)
            )
            dev.esan.sla_app.ui.email.EmailConfigScreen(
                viewModel = emailVM,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            val profileVM: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(dataStore = appContainer.dataStoreManager)
            )
            MainScreen(navController = navController) {
                ProfileScreen(
                    viewModel = profileVM,
                    onLogout = {
                        RetrofitClient.authInterceptor.setToken(null)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

// 🔥 ARQUITECTURA CORRECTA: Grafo de navegación anidado para compartir el ViewModel
private fun NavGraphBuilder.solicitudesGraph(navController: NavHostController, appContainer: AppContainer) {
    navigation(startDestination = Routes.SOLICITUDES_LIST, route = Routes.SOLICITUDES_GRAPH) {

        // PANTALLA DE LISTA
        composable(Routes.SOLICITUDES_LIST) { backStackEntry ->
            // Obtenemos el ViewModel asociado al grafo padre, garantizando que sea la misma instancia
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.SOLICITUDES_GRAPH) }
            val solicitudesVM: SolicitudesViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository))

            SolicitudesScreen(
                viewModel = solicitudesVM,
                onCrear = { navController.navigate(Routes.SOLICITUD_CREAR) },
                onEditar = { id -> navController.navigate(Routes.SOLICITUD_EDITAR.replace("{id}", id.toString())) }
            )
        }

        // PANTALLA DE CREAR
        composable(Routes.SOLICITUD_CREAR) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.SOLICITUDES_GRAPH) }
            val solicitudesVM: SolicitudesViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository))

            CrearSolicitudScreen(viewModel = solicitudesVM, onBack = { navController.popBackStack() })
        }

        // PANTALLA DE EDITAR
        composable(
            route = Routes.SOLICITUD_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.SOLICITUDES_GRAPH) }
            val solicitudesVM: SolicitudesViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository))

            val id = backStackEntry.arguments?.getInt("id")
            if (id != null) {
                EditarSolicitudScreen(id = id, viewModel = solicitudesVM, onBack = { navController.popBackStack() })
            }
        }
    }
}