package pe.fulbo.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class Posicion(val sigla: String, val nombre: String) {
    ARQUERO("ARQ", "Arquero"),
    DEFENSA("DEF", "Defensa"),
    MEDIOCAMPO("MED", "Mediocampo"),
    DELANTERO("DEL", "Delantero"),
}

@Serializable
enum class Nivel(val nombre: String) {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
}

@Serializable
enum class Formato(val etiqueta: String, val jugadoresPorEquipo: Int) {
    F5("5v5", 5),
    F7("7v7", 7),
    F11("11v11", 11),
}

@Serializable
enum class RolUsuario { JUGADOR, DUENO_CANCHA }

@Serializable
enum class Plan { GRATIS, PREMIUM }
