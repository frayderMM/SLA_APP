package dev.esan.sla_app.data.remote.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ExcelUploadApi {

    @Multipart
    @POST("/api/solicitudes/upload")
    suspend fun uploadExcel(
        @Part file: MultipartBody.Part
    ): ResponseBody
}