package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.excel.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ExcelUploadApi {

    @Multipart
    @POST("api/upload/excel")
    suspend fun uploadExcel(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>



}
