package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.ExcelUploadApi
import okhttp3.MultipartBody

class ExcelUploadRepository(private val api: ExcelUploadApi) {

    suspend fun uploadExcel(file: MultipartBody.Part) =
        api.uploadExcel(file)
}