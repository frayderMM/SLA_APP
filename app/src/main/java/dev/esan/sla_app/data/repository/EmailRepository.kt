package dev.esan.sla_app.data.repository

import android.content.Context
import androidx.work.*
import dev.esan.sla_app.data.remote.ResendClient
import dev.esan.sla_app.data.remote.api.ResendApi
import dev.esan.sla_app.data.remote.dto.ResendEmailRequest
import dev.esan.sla_app.utils.Constants
import dev.esan.sla_app.worker.EmailWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class EmailRepository(
    private val context: Context,
    private val alertasRepository: AlertasRepository
) {

    private val api: ResendApi = ResendClient.instance.create(ResendApi::class.java)

    // Send email immediately
    suspend fun sendEmailNow(toEmail: String): Result<String> {
        return try {
            val alertas = alertasRepository.cargarAlertas()
            val htmlContent = dev.esan.sla_app.utils.EmailFormatter.generateHtmlReport(alertas)

            val request = ResendEmailRequest(
                from = Constants.RESEND_FROM_EMAIL,
                to = listOf(toEmail),
                subject = "Reporte SLA (Manual)",
                html = htmlContent
            )
            val response = api.sendEmail(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.id ?: "Enviado")
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Schedule email
    fun scheduleEmailReport(intervalHours: Long, toEmail: String) {
        val data = workDataOf("TO_EMAIL" to toEmail)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<EmailWorker>(intervalHours, TimeUnit.HOURS)
            .setInputData(data)
            .setConstraints(constraints)
            .addTag("EMAIL_REPORT")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ScheduledEmailReport",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun scheduleDailyReport(hour: Int, minute: Int, toEmail: String) {
        val data = workDataOf("TO_EMAIL" to toEmail)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<EmailWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .addTag("EMAIL_REPORT")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ScheduledEmailReport",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
    
    fun cancelScheduledReport() {
        WorkManager.getInstance(context).cancelUniqueWork("ScheduledEmailReport")
    }
}
