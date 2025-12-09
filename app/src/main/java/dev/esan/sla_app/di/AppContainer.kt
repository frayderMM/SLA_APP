package dev.esan.sla_app.di

import android.content.Context
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.data.remote.api.*
import dev.esan.sla_app.data.remote.repository.ExportExcelRepository
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

    val assistantApi: AssistantApi
    val assistantRepository: AssistantRepository
    val dataStoreManager: DataStoreManager

    val excelUploadRepository: ExcelUploadRepository
    val exportExcelRepository: ExportExcelRepository  // ✅ AGREGADO
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
    override val assistantApi: AssistantApi by lazy { retrofit.create(AssistantApi::class.java) }
    private val exportExcelApi: ExportExcelApi by lazy { retrofit.create(ExportExcelApi::class.java) } // ✅ API NUEVA
    private val excelUploadApi: ExcelUploadApi by lazy { retrofit.create(ExcelUploadApi::class.java) }

    // Repositories
    override val authRepository: AuthRepository by lazy { AuthRepository(authApi) }
    override val insightRepository: InsightRepository by lazy { InsightRepository(dashboardApi) }
    override val slaRepository: SlaRepository by lazy { SlaRepository(slaApi, tiposSlaApi) }
    override val alertasRepository: AlertasRepository by lazy { AlertasRepository(alertasApi) }
    override val solicitudesRepository: SolicitudesRepository by lazy {
        SolicitudesRepository(solicitudesApi, tiposSlaApi)
    }
    override val reportesRepository: ReportesRepository by lazy { ReportesRepository(reportesApi) }
    override val profileRepository: ProfileRepository by lazy { ProfileRepository(profileApi) }
    override val emailRepository: EmailRepository by lazy { EmailRepository(context, alertasRepository) }
    override val assistantRepository: AssistantRepository by lazy { AssistantRepository(assistantApi) }

    override val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(context.applicationContext)
    }

    override val excelUploadRepository: ExcelUploadRepository by lazy {
        ExcelUploadRepository(excelUploadApi, dataStoreManager, context)
    }

    override val exportExcelRepository: ExportExcelRepository by lazy {
        ExportExcelRepository(exportExcelApi, dataStoreManager)
    }
}
