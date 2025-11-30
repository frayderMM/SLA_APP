package dev.esan.sla_app.data.remote

import dev.esan.sla_app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 1. Creamos una única instancia pública de nuestro interceptor con estado.
    val authInterceptor = AuthInterceptor()

    // 2. El cliente OkHttp ahora usa esta única instancia y es privado.
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Se añade la instancia NO BLOQUEANTE
            .addInterceptor(logging)
            .build()
    }

    // 3. El cliente Retrofit ahora es una instancia 'lazy' singleton que no necesita parámetros.
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}