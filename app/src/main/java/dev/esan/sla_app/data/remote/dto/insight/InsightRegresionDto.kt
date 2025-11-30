package dev.esan.sla_app.data.remote.dto.insight

data class InsightRegresionDto(
    val tipoSla: String,
    val regresion: RegresionValores,
    val historico: List<InsightPoint>,
    val proyeccion: ProyeccionDto,
    val recomendacion: String
)

data class RegresionValores(
    val pendiente: Double,
    val intercepto: Double,
    val r2: Double
)

data class ProyeccionDto(
    val periodoSiguiente: String,
    val valor: Double,
    val nivelRiesgo: String
)
