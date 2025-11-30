package dev.esan.sla_app.utils

/**
 * Una clase genérica que encapsula el resultado de una operación de datos, 
 * indicando su estado (Success, Error, Loading) y conteniendo los datos o un mensaje de error.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    /**
     * Representa un estado de éxito, conteniendo los datos solicitados.
     * @param data Los datos obtenidos de la fuente.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Representa un estado de error, conteniendo un mensaje que describe el problema.
     * @param message El mensaje de error.
     * @param data Datos opcionales que se pueden querer mantener (ej. datos de caché).
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Representa un estado de carga, indicando que la operación está en progreso.
     */
    class Loading<T> : Resource<T>()
}
