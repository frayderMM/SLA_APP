package dev.esan.sla_app.data.repository

import android.content.Context
import android.net.Uri
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.remote.api.ExcelUploadApi
import dev.esan.sla_app.data.remote.dto.excel.UploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ExcelUploadRepository(
    private val api: ExcelUploadApi,
    private val dataStore: DataStoreManager,
    private val context: Context
) {
    suspend fun upload(uri: Uri): Result<UploadResponse> =
        withContext(Dispatchers.IO) {
            try {
                // Leer archivo
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    ?: return@withContext Result.failure(Exception("Archivo inválido"))

                // Crear RequestBody
                val body = bytes.toRequestBody(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaType()
                )

                // Crear parte del multipart
                val part = MultipartBody.Part.createFormData(
                    "file",
                    "solicitudes.xlsx",
                    body
                )

                // ❗ NO enviar token manualmente
                // El interceptor ya agrega Authorization
                val response = api.uploadExcel(part)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
