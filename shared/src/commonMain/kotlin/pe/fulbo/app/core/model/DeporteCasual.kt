package pe.fulbo.app.core.model

import kotlinx.serialization.Serializable

/**
 * Deportes casuales soportados por la aplicación.
 */
@Serializable
enum class DeporteCasual(val id: String, val nombre: String, val emoji: String) {
    FUTBOL("futbol", "Fútbol", "⚽"),
    BASQUET("basquet", "Básquet", "🏀"),
    VOLEY("voley", "Vóley", "🏐");

    companion object {
        fun de(id: String): DeporteCasual = entries.firstOrNull { it.id == id } ?: FUTBOL

        fun obtenerPosiciones(deporte: DeporteCasual): List<String> = when (deporte) {
            FUTBOL -> listOf(
                "Arquero",
                "Defensa central",
                "Lateral",
                "Mediocampista",
                "Extremo",
                "Delantero",
            )
            BASQUET -> listOf(
                "Base",
                "Escolta",
                "Alero",
                "Ala-pívot",
                "Pívot",
            )
            VOLEY -> listOf(
                "Armador",
                "Opuesto",
                "Punta",
                "Central",
                "Líbero",
            )
        }

        fun obtenerFormatos(deporte: DeporteCasual): List<String> = when (deporte) {
            FUTBOL -> listOf("4 vs 4", "5 vs 5", "7 vs 7", "11 vs 11")
            BASQUET -> listOf("3 vs 3", "4 vs 4", "5 vs 5")
            VOLEY -> listOf("2 vs 2", "4 vs 4", "6 vs 6")
        }

        val niveles: List<String> = listOf("Recién empiezo", "Casual", "Intermedio", "Competitivo")
    }
}

/**
 * Sistema de gamificación de rangos por equipo según semanas consecutivas de juego.
 */
@Serializable
enum class RangoEquipo(
    val id: String,
    val titulo: String,
    val semanas: String,
    val descripcionAro: String,
    val semanasMeta: Int,
) {
    BRONCE("bronce", "Bronce", "Equipo nuevo", "Aro simple", 1),
    PLATA("plata", "Plata", "4 semanas seguidas", "Aro plateado", 4),
    ORO("oro", "Oro", "12 semanas seguidas", "Aro dorado con halo", 12),
    PLATINO("platino", "Platino", "26 semanas seguidas", "Aro luminoso de colores", 26),
    LEYENDA("leyenda", "Leyenda", "52 semanas seguidas", "Aro de fuego con brillo", 52);

    companion object {
        fun de(id: String): RangoEquipo = entries.firstOrNull { it.id == id } ?: BRONCE
    }
}

/**
 * Configuración de preferencias y posiciones de un usuario en un deporte específico.
 */
@Serializable
data class DeportePerfilConfig(
    val deporte: DeporteCasual,
    val posiciones: List<String> = emptyList(),
    val nivel: String = "Casual",
    val isCompletado: Boolean = false,
)

/**
 * Representa una tarjeta de partido casual en el feed de búsqueda.
 */
@Serializable
data class PartidoCasualItem(
    val id: String,
    val deporte: DeporteCasual,
    val cancha: String,
    val fechaHora: String,
    val formato: String,
    val nivel: String,
    val costoPersona: Int,
    val cuposFaltantes: Int,
    val organizadorNombre: String,
    val organizadorRating: Double,
    val organizadorIniciales: String,
    val estadoTexto: String,
)

/**
 * Resultado registrado de un partido entre dos equipos.
 */
@Serializable
data class ResultadoPartidoItem(
    val id: String,
    val local: String,
    val golesLocal: Int,
    val visitante: String,
    val golesVisitante: Int,
    val estado: String, // "Ganó", "Empate", "Perdió"
)

/**
 * Reseña de reputación otorgada a un jugador.
 */
@Serializable
data class ResenaJugador(
    val id: String,
    val texto: String,
    val autor: String,
)

/**
 * Información de una cancha visible en el mapa interactivo.
 */
@Serializable
data class CanchaMapaItem(
    val id: String,
    val nombre: String,
    val direccion: String,
    val horario: String,
    val distancia: String,
    val isAbierto: Boolean,
    val latRelativa: Float,
    val lngRelativa: Float,
)
