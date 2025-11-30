package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.ProfileApi

class ProfileRepository(
    private val api: ProfileApi
) {
    suspend fun getProfile() = api.getProfile()
}
