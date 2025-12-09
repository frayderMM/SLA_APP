package dev.esan.sla_app.data.remote

import dev.esan.sla_app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Interceptor que agrega el token a las peticiones
    val authInterceptor = AuthInterceptor()

    // Cliente OkHttp
    private val okHttpClient: OkHttpClient by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // 🔥 FIX OBLIGATORIO PARA CLOUDFLARE / RENDER
                val newRequest = chain.request().newBuilder()
                    .addHeader("Accept", "application/json") // <<--- AQUÍ EL FIX
                    .build()

                chain.proceed(newRequest)
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    // Instancia Retrofit
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
