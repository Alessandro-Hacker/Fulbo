package pe.fulbo.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.jetbrains.compose.resources.stringResource
import pe.fulbo.app.core.designsystem.component.PlaceholderScreen
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.feature.auth.WelcomeScreen
import pe.fulbo.app.feature.canchas.CanchasRoute
import pe.fulbo.app.feature.canchas.MapaScreen
import pe.fulbo.app.feature.equipo.AlineacionScreen
import pe.fulbo.app.feature.equipo.EditarEquipoScreen
import pe.fulbo.app.feature.equipo.EquipoScreen
import pe.fulbo.app.feature.equipo.MarcosRangoScreen
import pe.fulbo.app.feature.equipo.RegistrarResultadoScreen
import pe.fulbo.app.feature.onboarding.CreaTuPerfilScreen
import pe.fulbo.app.feature.onboarding.TusDeportesScreen
import pe.fulbo.app.feature.partidos.PartidosScreen
import pe.fulbo.app.feature.partidos.PublicarPartidoScreen
import pe.fulbo.app.feature.perfil.PerfilScreen

@Composable
fun FulboNavHost(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val destino = backStack?.destination
    val mostrarBarra = Pestana.entries.any { destino.esRuta(it.ruta) }

    Scaffold(
        containerColor = FulboColors.Background,
        bottomBar = { if (mostrarBarra) FulboBottomBar(navController, destino) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Bienvenida,
            modifier = Modifier.padding(padding),
        ) {
            // ---------- Onboarding y Autenticación ----------
            composable<Bienvenida> {
                val entrar = {
                    navController.navigate(Partidos) { popUpTo(Bienvenida) { inclusive = true } }
                }
                WelcomeScreen(
                    onCrearCuenta = { navController.navigate(CreaTuPerfil) },
                    onGoogle = entrar,
                    onNumero = entrar,
                    onIniciarSesion = entrar,
                )
            }

            // Paso 1 de 2: Crea tu perfil (Mockup página 1)
            composable<CreaTuPerfil> {
                CreaTuPerfilScreen(
                    onSiguiente = { _, _, _ ->
                        navController.navigate(TusDeportes)
                    },
                )
            }

            // Paso 2 de 2: Tus deportes (Mockups páginas 11, 12, 13)
            composable<TusDeportes> {
                TusDeportesScreen(
                    onCrearPerfil = {
                        navController.navigate(Partidos) {
                            popUpTo(Bienvenida) { inclusive = true }
                        }
                    },
                )
            }

            composable<Registro> { PlaceholderScreen("Registro", "M1 · Registro y perfil") }
            composable<Login> { PlaceholderScreen("Iniciar sesión", "M1 · Registro y perfil") }

            // ---------- 4 Pestañas principales de los Mockups ----------
            // 1. Partidos cerca (Mockup página 2)
            composable<Partidos> {
                PartidosScreen(
                    onPublicarPartido = { navController.navigate(PublicarPartido()) },
                )
            }

            // 2. Mapa y Buscar canchas (Mockup página 5)
            composable<Mapa> {
                MapaScreen(
                    onCrearPartidoAqui = { cancha ->
                        navController.navigate(PublicarPartido(cancha = cancha))
                    },
                )
            }

            // 3. Mi equipo: Los Pibes del Jueves (Mockup página 6)
            composable<Equipo> {
                EquipoScreen(
                    onRegistrarPartido = { navController.navigate(RegistrarResultado) },
                    onVerAlineacion = { navController.navigate(Alineacion) },
                    onVerMarcos = { navController.navigate(MarcosRango) },
                    onEditarEquipo = { navController.navigate(EditarEquipo) },
                )
            }

            // 4. Perfil de usuario (Mockup página 4)
            composable<Perfil> {
                PerfilScreen()
            }

            // ---------- Vistas de Detalle y Flujos Secundarios ----------
            // Publicar partido (Mockup página 3)
            composable<PublicarPartido> { entry ->
                val ruta = entry.toRoute<PublicarPartido>()
                PublicarPartidoScreen(
                    canchaInicial = ruta.cancha,
                    onVolver = { navController.popBackStack() },
                    onPartidoPublicado = { navController.popBackStack() },
                )
            }

            // Alineación táctica (Mockup página 7)
            composable<Alineacion> {
                AlineacionScreen(
                    onVolver = { navController.popBackStack() },
                )
            }

            // Tu equipo: configuración y edición (Mockup página 8)
            composable<EditarEquipo> {
                EditarEquipoScreen(
                    onVolver = { navController.popBackStack() },
                    onVerMarcos = { navController.navigate(MarcosRango) },
                    onGuardarEquipo = { navController.popBackStack() },
                )
            }

            // Marcos por rango (Mockup página 9)
            composable<MarcosRango> {
                MarcosRangoScreen(
                    onVolver = { navController.popBackStack() },
                )
            }

            // Registrar resultado (Mockup página 10)
            composable<RegistrarResultado> {
                RegistrarResultadoScreen(
                    onVolver = { navController.popBackStack() },
                    onVerAlineaciones = { navController.navigate(Alineacion) },
                    onGuardarResultado = { navController.popBackStack() },
                )
            }

            // Compatibilidad
            composable<Canchas> {
                CanchasRoute(onCanchaClick = { id -> navController.navigate(CanchaDetalle(id)) })
            }
            composable<CanchaDetalle> { entry ->
                val ruta = entry.toRoute<CanchaDetalle>()
                PlaceholderScreen("Cancha ${ruta.canchaId}", "M2 · Canchas")
            }
            composable<Reservar> { PlaceholderScreen("Reservar", "M3 · Reservas y pagos") }
            composable<SalaPartido> { PlaceholderScreen("Sala del partido", "M4 · Partidos") }
            composable<Premium> { PlaceholderScreen("Premium", "M7 · Monetización") }
        }
    }
}

@Composable
private fun FulboBottomBar(navController: NavHostController, destino: NavDestination?) {
    NavigationBar(containerColor = FulboColors.Background) {
        Pestana.entries.forEach { tab ->
            NavigationBarItem(
                selected = destino.esRuta(tab.ruta),
                onClick = {
                    navController.navigate(tab.ruta) {
                        popUpTo(Partidos) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icono, contentDescription = null) },
                label = { Text(stringResource(tab.titulo)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FulboColors.Green,
                    selectedTextColor = FulboColors.Green,
                    indicatorColor = FulboColors.GreenContainer,
                    unselectedIconColor = FulboColors.TextSecondary,
                    unselectedTextColor = FulboColors.TextSecondary,
                ),
            )
        }
    }
}

private fun NavDestination?.esRuta(ruta: Any): Boolean {
    val clase: kotlin.reflect.KClass<*> = ruta::class
    return this?.hierarchy?.any { it.hasRoute(clase) } == true
}
