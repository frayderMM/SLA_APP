package dev.esan.sla_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import dev.esan.sla_app.ui.regression.RegressionScreen
import dev.esan.sla_app.ui.security.SecurityScreen // <-- IMPORTAR LA NUEVA PANTALLA
import dev.esan.sla_app.ui.sla.*
import dev.esan.sla_app.ui.solicitudes.*

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val context = LocalContext.current
    val appContainer: AppContainer = remember(context) {
        DefaultAppContainer(context)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
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
                navController.navigate(Routes.DASHBOARD_GRAPH) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }

        dashboardGraph(navController, appContainer)

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
                factory = ProfileViewModelFactory(
                    dataStore = appContainer.dataStoreManager,
                    authRepository = appContainer.authRepository
                )
            )
            MainScreen(navController = navController) {
                ProfileScreen(
                    viewModel = profileVM,
                    onLogout = {
                        RetrofitClient.authInterceptor.setToken(null)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    // --- CONECTAR LA NAVEGACIÓN ---
                    onNavigateToSecurity = { navController.navigate(Routes.SECURITY) }
                )
            }
        }

        // --- AÑADIR LA NUEVA PANTALLA AL GRAFO ---
        composable(Routes.SECURITY) {
            val profileVM: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(
                    dataStore = appContainer.dataStoreManager,
                    authRepository = appContainer.authRepository
                )
            )
            SecurityScreen(
                viewModel = profileVM,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ... (El resto del archivo se mantiene igual)

private fun NavGraphBuilder.dashboardGraph(navController: NavHostController, appContainer: AppContainer) {
    navigation(startDestination = Routes.DASHBOARD, route = Routes.DASHBOARD_GRAPH) {
        composable(Routes.DASHBOARD) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.DASHBOARD_GRAPH) }
            val insightVM: InsightPanelViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = InsightPanelViewModelFactory(appContainer.insightRepository)
            )

            MainScreen(navController = navController) {
                DashboardScreen(
                    viewModel = insightVM,
                    onNavigateToAlerts = { navController.navigate(Routes.ALERTAS) },
                    onNavigateToRegression = { navController.navigate(Routes.REGRESSION) },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
                )
            }
        }
        composable(Routes.REGRESSION) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.DASHBOARD_GRAPH) }
            val insightVM: InsightPanelViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = InsightPanelViewModelFactory(appContainer.insightRepository)
            )

            RegressionScreen(viewModel = insightVM) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.solicitudesGraph(navController: NavHostController, appContainer: AppContainer) {
    navigation(startDestination = Routes.SOLICITUDES_LIST, route = Routes.SOLICITUDES_GRAPH) {
        composable(Routes.SOLICITUDES_LIST) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.SOLICITUDES_GRAPH) }
            val solicitudesVM: SolicitudesViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository))

            SolicitudesScreen(
                viewModel = solicitudesVM,
                onCrear = { navController.navigate(Routes.SOLICITUD_CREAR) },
                onEditar = { id -> navController.navigate(Routes.SOLICITUD_EDITAR.replace("{id}", id.toString())) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SOLICITUD_CREAR) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.SOLICITUDES_GRAPH) }
            val solicitudesVM: SolicitudesViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository))

            CrearSolicitudScreen(viewModel = solicitudesVM, onBack = { navController.popBackStack() })
        }
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