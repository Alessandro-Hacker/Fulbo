package pe.fulbo.app.feature.canchas.domain

/** Modelo de dominio: lo que la UI necesita, independiente del DTO de la API o la entidad de Room. */
data class Cancha(
    val id: String,
    val nombre: String,
    val distrito: String,
    val superficie: String,
    val formato: String,
    val techada: Boolean,
    val rating: Double,
    val precioHora: Double,
    val distanciaMetros: Int,
    val proximoLibre: String?,
    val ultimaHora: Boolean,
) {
    val distanciaTexto: String
        get() = if (distanciaMetros < 1000) "$distanciaMetros m"
        else "%.1f km".format(distanciaMetros / 1000.0)
}
