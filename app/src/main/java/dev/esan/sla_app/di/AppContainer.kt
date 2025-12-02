package dev.esan.sla_app.di

import android.content.Context
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.data.remote.api.*
import dev.esan.sla_app.data.repository.*

interface AppContainer {
    val authRepository: AuthRepository
    val insightRepository: InsightRepository
    val slaRepository: SlaRepository
    val alertasRepository: AlertasRepository
    val solicitudesRepository: SolicitudesRepository
    val reportesRepository: ReportesRepository
    val profileRepository: ProfileRepository
    val emailRepository: EmailRepository
    val dataStoreManager: DataStoreManager
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val retrofit = RetrofitClient.instance

    // APIs
    private val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    private val dashboardApi: DashboardApi by lazy { retrofit.create(DashboardApi::class.java) }
    private val slaApi: SlaApi by lazy { retrofit.create(SlaApi::class.java) }
    private val alertasApi: AlertasApi by lazy { retrofit.create(AlertasApi::class.java) }
    private val solicitudesApi: SolicitudesApi by lazy { retrofit.create(SolicitudesApi::class.java) }
    private val tiposSlaApi: TiposSlaApi by lazy { retrofit.create(TiposSlaApi::class.java) }
    private val reportesApi: ReportesApi by lazy { retrofit.create(ReportesApi::class.java) }
    private val profileApi: ProfileApi by lazy { retrofit.create(ProfileApi::class.java) }

    // Repositories
    override val authRepository: AuthRepository by lazy { AuthRepository(authApi) }
    override val insightRepository: InsightRepository by lazy { InsightRepository(dashboardApi) }

    // 🔥 CORREGIDO: SlaRepository ahora recibe ambas dependencias
    override val slaRepository: SlaRepository by lazy {
        SlaRepository(slaApi, tiposSlaApi)
    }

    override val alertasRepository: AlertasRepository by lazy { AlertasRepository(alertasApi) }
    override val solicitudesRepository: SolicitudesRepository by lazy {
        SolicitudesRepository(solicitudesApi, tiposSlaApi)
    }
    override val reportesRepository: ReportesRepository by lazy { ReportesRepository(reportesApi) }
    override val profileRepository: ProfileRepository by lazy { ProfileRepository(profileApi) }
    override val emailRepository: EmailRepository by lazy { EmailRepository(context, alertasRepository) }

    override val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(context.applicationContext)
    }
}
