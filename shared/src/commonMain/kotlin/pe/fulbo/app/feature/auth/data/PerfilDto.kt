package pe.fulbo.app.feature.auth.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento `usuarios/{uid}`. Los nombres de campo deben coincidir con firebase/firestore.rules
 * (la regla de `create` rechaza cualquier campo que no esté en su lista).
 */
@Serializable
data class PerfilDto(
    val nombre: String,
    val apellido: String,
    val alias: String,
    val deportes: Map<String, DeporteDto>,
    val mayor_de_16: Boolean,
    val ciudad: String? = null,
    val ubicacion: String? = null,
    val foto_url: String? = null,
    val rep_global: Double? = null,
    val rep_puntualidad: Double? = null,
    val rep_asistencia: Double? = null,
    val rep_pago: Double? = null,
    val rep_trato: Double? = null,
    val num_calificaciones: Int = 0,
    val partidos_jugados: Int = 0,
    val faltas: Int = 0,
    val plan: String = "gratuito",
    val plan_vence_en: String? = null,
    val creado_en: BaseTimestamp = Timestamp.ServerTimestamp,
)

@Serializable
data class DeporteDto(
    val posiciones: List<String>,
    val nivel: String,
)
