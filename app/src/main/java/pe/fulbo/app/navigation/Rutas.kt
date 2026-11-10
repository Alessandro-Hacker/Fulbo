package pe.fulbo.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import pe.fulbo.app.R

/**
 * Rutas con navegación type-safe. Para agregar una pantalla:
 *  1. Declara aquí un @Serializable object (sin argumentos) o data class (con argumentos).
 *  2. Regístrala en FulboNavHost con composable<TuRuta> { … }.
 */

// Onboarding / auth (M1)
@Serializable data object Bienvenida
@Serializable data object Registro
@Serializable data object Login

// Pestañas principales
@Serializable data object Canchas
@Serializable data object Partidos
@Serializable data object Equipo
@Serializable data object Liga
@Serializable data object Perfil

// Detalles y flujos
@Serializable data class CanchaDetalle(val canchaId: String)
@Serializable data class Reservar(val canchaId: String)
@Serializable data object CrearPartido
@Serializable data class SalaPartido(val partidoId: String)
@Serializable data object Premium

enum class Pestana(val ruta: Any, val titulo: Int, val icono: ImageVector) {
    CANCHAS(Canchas, R.string.tab_canchas, Icons.Outlined.Place),
    PARTIDOS(Partidos, R.string.tab_partidos, Icons.Outlined.SportsSoccer),
    EQUIPO(Equipo, R.string.tab_equipo, Icons.Outlined.Groups),
    LIGA(Liga, R.string.tab_liga, Icons.Outlined.EmojiEvents),
    PERFIL(Perfil, R.string.tab_perfil, Icons.Outlined.Person),
}
