package dev.esan.sla_app.utils

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor pasivo y seguro para la autenticación.
 * Esta clase NO lee de DataStore. Simplemente mantiene el token en memoria.
 * El token es inyectado de forma asíncrona desde otras partes de la app,
 * como el AppNavHost al inicio o el LoginViewModel tras un login exitoso.
 */
class AuthInterceptor : Interceptor {
    @Volatile
    private var token: String? = null

    fun setToken(token: String?) {
        this.token = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Lee el token de la variable en memoria. Esto es instantáneo y no bloquea.
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
