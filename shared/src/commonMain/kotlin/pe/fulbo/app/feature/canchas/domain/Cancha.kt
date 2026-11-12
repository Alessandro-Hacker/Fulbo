package pe.fulbo.app.feature.canchas.domain

/** Modelo de dominio: lo que la UI necesita, independiente del DTO de Firestore. */
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
    /** "650 m" o "2.4 km" (sin String.format: no existe en Kotlin común / iOS). */
    val distanciaTexto: String
        get() = if (distanciaMetros < 1000) "$distanciaMetros m"
        else {
            val decimas = (distanciaMetros + 50) / 100   // redondea a 0.1 km
            "${decimas / 10}.${decimas % 10} km"
        }
}
