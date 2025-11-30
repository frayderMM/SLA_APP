package dev.esan.sla_app.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor con estado para añadir dinámicamente el token de autenticación.
 * Esta es la versión NO BLOQUEANTE y definitiva.
 */
class AuthInterceptor : Interceptor {

    // Se usa @Volatile para asegurar que los cambios sean visibles en todos los hilos.
    @Volatile
    private var token: String? = null

    /**
     * Método para actualizar el token desde fuera del interceptor.
     * Se llamará de forma asíncrona al iniciar la app.
     */
    fun setToken(token: String?) {
        this.token = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Añade el token que tiene guardado en memoria, si existe.
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}