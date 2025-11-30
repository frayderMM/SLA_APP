package dev.esan.sla_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.di.DefaultAppContainer
import dev.esan.sla_app.ui.alertas.*
import dev.esan.sla_app.ui.dashboard.*
import dev.esan.sla_app.ui.insight.InsightIndicatorsViewModel
import dev.esan.sla_app.ui.insight.InsightPanelScreen
import dev.esan.sla_app.ui.insight.InsightPanelViewModel
import dev.esan.sla_app.ui.insight.InsightPanelViewModelFactory
import dev.esan.sla_app.ui.login.*
import dev.esan.sla_app.ui.pdf.*
import dev.esan.sla_app.ui.profile.*
import dev.esan.sla_app.ui.sla.*
import dev.esan.sla_app.ui.solicitudes.*
import kotlinx.coroutines.flow.first

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val context = LocalContext.current
    val appContainer = remember(context) {
        DefaultAppContainer(context)
    }

    // --- VERIFICACIÓN DE SESIÓN Y CARGA DEL TOKEN ---
    LaunchedEffect(Unit) {
        val token = appContainer.dataStoreManager.token.first()

        if (token != null) {
            RetrofitClient.authInterceptor.setToken(token)
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // --- El NavHost ahora empieza en una ruta de carga neutral ---
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
                factory = LoginViewModelFactory(
                    appContainer.authRepository,
                    appContainer.dataStoreManager
                )
            )
            LoginScreen(
                viewModel = loginVM,
                onSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }


        composable(Routes.DASHBOARD) {

            val insightVM: InsightPanelViewModel = viewModel(
                factory = InsightPanelViewModelFactory(appContainer.insightRepository)
            )

            MainScreen(navController = navController) {
                DashboardScreen(
                    viewModel = insightVM,
                    onNavigateToSolicitudes = { navController.navigate(Routes.SOLICITUDES) }
                )
            }
        }





        composable(Routes.INDICADORES) {
            val indicadoresVM: IndicadoresViewModel = viewModel(
                factory = IndicadoresViewModelFactory(appContainer.slaRepository)
            )
            MainScreen(navController = navController) {
                IndicadoresScreen(viewModel = indicadoresVM)
            }
        }

        composable(Routes.ALERTAS) {
            val alertasVM: AlertasViewModel = viewModel(
                factory = AlertasViewModelFactory(appContainer.alertasRepository)
            )
            MainScreen(navController = navController) {
                AlertasScreen(viewModel = alertasVM)
            }
        }

        composable(Routes.PDF) {
            val pdfVM: PdfViewModel = viewModel(
                factory = PdfViewModelFactory(appContainer.reportesRepository)
            )
            MainScreen(navController = navController) {
                PdfScreen(viewModel = pdfVM)
            }
        }

        composable(Routes.PROFILE) {

            // 🔥 ViewModel REAL usando solo el DataStoreManager
            val profileVM: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(
                    dataStore = appContainer.dataStoreManager
                )
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


        composable(Routes.SOLICITUDES) {
            val solicitudesVM: SolicitudesViewModel = viewModel(
                factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository)
            )
            MainScreen(navController = navController) {
                SolicitudesScreen(
                    viewModel = solicitudesVM,
                    onCrear = { navController.navigate(Routes.SOLICITUD_CREAR) },
                    onEditar = { id -> navController.navigate("solicitudes/editar/$id") }
                )
            }
        }

        composable(Routes.SOLICITUD_CREAR) {
            val solicitudesVM: SolicitudesViewModel = viewModel(
                factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository)
            )
            CrearSolicitudScreen(viewModel = solicitudesVM, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.SOLICITUD_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val solicitudesVM: SolicitudesViewModel = viewModel(
                factory = SolicitudesViewModelFactory(appContainer.solicitudesRepository)
            )
            if (id != null) {
                EditarSolicitudScreen(id = id, viewModel = solicitudesVM, onBack = { navController.popBackStack() })
            }
        }
    }
}