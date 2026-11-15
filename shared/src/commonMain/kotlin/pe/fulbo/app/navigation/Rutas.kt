package pe.fulbo.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import pe.fulbo.app.resources.Res
import pe.fulbo.app.resources.tab_equipo
import pe.fulbo.app.resources.tab_mapa
import pe.fulbo.app.resources.tab_partidos
import pe.fulbo.app.resources.tab_perfil

/**
 * Rutas type-safe de la aplicación.
 */

// Onboarding y bienvenida
@Serializable data object Bienvenida
@Serializable data object CreaTuPerfil
@Serializable data object TusDeportes
@Serializable data object Registro
@Serializable data object Login

// 4 Pestañas principales de la barra inferior según los mockups
@Serializable data object Partidos
@Serializable data object Mapa
@Serializable data object Equipo
@Serializable data object Perfil

// Vistas y flujos detallados
@Serializable data class PublicarPartido(val cancha: String = "Cancha Los Álamos")
@Serializable data object Alineacion
@Serializable data object EditarEquipo
@Serializable data object MarcosRango
@Serializable data object RegistrarResultado

// Compatibilidad
@Serializable data object Canchas
@Serializable data class CanchaDetalle(val canchaId: String)
@Serializable data class Reservar(val canchaId: String)
@Serializable data class SalaPartido(val partidoId: String)
@Serializable data object Premium

enum class Pestana(val ruta: Any, val titulo: StringResource, val icono: ImageVector) {
    PARTIDOS(Partidos, Res.string.tab_partidos, Icons.Outlined.SportsSoccer),
    MAPA(Mapa, Res.string.tab_mapa, Icons.Outlined.Place),
    EQUIPO(Equipo, Res.string.tab_equipo, Icons.Outlined.Groups),
    PERFIL(Perfil, Res.string.tab_perfil, Icons.Outlined.Person),
}
