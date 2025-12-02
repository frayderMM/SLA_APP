package dev.esan.sla_app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.esan.sla_app.data.remote.ResendClient
import dev.esan.sla_app.data.remote.api.ResendApi
import dev.esan.sla_app.data.remote.dto.ResendEmailRequest
import dev.esan.sla_app.utils.Constants

class EmailWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val toEmail = inputData.getString("TO_EMAIL") ?: return Result.failure()

        return try {
            // Manually create dependencies since WorkerFactory is not set up
            val retrofit = dev.esan.sla_app.data.remote.RetrofitClient.instance
            val alertasApi = retrofit.create(dev.esan.sla_app.data.remote.api.AlertasApi::class.java)
            val alertas = alertasApi.getAlertas()
            
            val htmlContent = dev.esan.sla_app.utils.EmailFormatter.generateHtmlReport(alertas)

            val api = ResendClient.instance.create(ResendApi::class.java)
            val request = ResendEmailRequest(
                from = Constants.RESEND_FROM_EMAIL,
                to = listOf(toEmail),
                subject = "Reporte SLA (Programado)",
                html = htmlContent
            )

            val response = api.sendEmail(request)
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
