package pe.fulbo.app.core.model

import kotlinx.serialization.Serializable

// Los `valor` coinciden con los strings que guarda Firestore y que validan firestore.rules.

@Serializable
enum class Posicion(val sigla: String, val nombre: String) {
    ARQUERO("ARQ", "Arquero"),
    DEFENSA("DEF", "Defensa"),
    MEDIOCAMPO("MED", "Mediocampo"),
    DELANTERO("DEL", "Delantero"),
}

@Serializable
enum class Nivel(val valor: String, val nombre: String) {
    RECIEN("recien", "Recién empiezo"),
    CASUAL("casual", "Casual"),
    INTERMEDIO("intermedio", "Intermedio"),
    COMPETITIVO("competitivo", "Competitivo");

    companion object {
        fun de(valor: String): Nivel = entries.first { it.valor == valor }
    }
}

@Serializable
enum class Formato(val valor: String, val etiqueta: String, val jugadoresPorEquipo: Int) {
    F4("4v4", "4v4", 4),
    F5("5v5", "5v5", 5),
    F7("7v7", "7v7", 7),
    F11("11v11", "11v11", 11);

    companion object {
        fun de(valor: String): Formato = entries.first { it.valor == valor }
    }
}

@Serializable
enum class EstadoPartido(val valor: String) {
    PUBLICADO("publicado"),
    COMPLETO("completo"),
    EN_JUEGO("en_juego"),
    EN_CALIFICACION("en_calificacion"),
    CERRADO("cerrado"),
    CANCELADO("cancelado"),
    VENCIDO("vencido");

    companion object {
        fun de(valor: String): EstadoPartido = entries.first { it.valor == valor }
    }
}

@Serializable
enum class RolUsuario { JUGADOR, DUENO_CANCHA }

@Serializable
enum class Plan(val valor: String) {
    GRATIS("gratuito"),
    PREMIUM("premium");

    companion object {
        fun de(valor: String): Plan = entries.first { it.valor == valor }
    }
}
