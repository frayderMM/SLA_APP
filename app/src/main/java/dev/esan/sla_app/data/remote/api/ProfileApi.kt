package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.profile.ProfileDto
import retrofit2.http.GET

interface ProfileApi {

    @GET("/api/auth/me")
    suspend fun getProfile(): ProfileDto
}
