package dev.esan.sla_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ResendEmailRequest(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: List<String>,
    @SerializedName("subject") val subject: String,
    @SerializedName("html") val html: String
)

data class ResendEmailResponse(
    @SerializedName("id") val id: String
)
