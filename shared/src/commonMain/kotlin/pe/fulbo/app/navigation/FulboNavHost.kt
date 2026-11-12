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
            // TODO(M1 auth): elegir según Firebase Auth (sin sesión → Bienvenida; sin correo verificado →
            // "Revisa tu correo"; sin perfil → Completar perfil; con perfil → Canchas)
            startDestination = Bienvenida,
            modifier = Modifier.padding(padding),
        ) {
            // ---------- Onboarding ----------
            composable<Bienvenida> {
                val entrar = {
                    navController.navigate(Canchas) { popUpTo(Bienvenida) { inclusive = true } }
                }
                WelcomeScreen(
                    onCrearCuenta = { navController.navigate(Registro) },
                    onGoogle = entrar,   // TODO(M1): login con Google
                    onNumero = entrar,   // TODO(M1): login con número
                    onIniciarSesion = { navController.navigate(Login) },
                )
            }
            composable<Registro> { PlaceholderScreen("Registro", "M1 · Registro y perfil") }
            composable<Login> { PlaceholderScreen("Iniciar sesión", "M1 · Registro y perfil") }

            // ---------- Pestañas ----------
            composable<Canchas> {
                CanchasRoute(onCanchaClick = { id -> navController.navigate(CanchaDetalle(id)) })
            }
            composable<Partidos> { PlaceholderScreen("Partidos", "M4 · Partidos") }
            composable<Equipo> { PlaceholderScreen("Mi equipo", "M5 · Equipos y liga") }
            composable<Liga> { PlaceholderScreen("Liga", "M5 · Equipos y liga") }
            composable<Perfil> { PlaceholderScreen("Perfil", "M1 · Registro y perfil") }

            // ---------- Detalles ----------
            composable<CanchaDetalle> { entry ->
                val ruta = entry.toRoute<CanchaDetalle>()
                PlaceholderScreen("Cancha ${ruta.canchaId}", "M2 · Canchas")
            }
            composable<Reservar> { PlaceholderScreen("Reservar", "M3 · Reservas y pagos") }
            composable<CrearPartido> { PlaceholderScreen("Crear partido", "M4 · Partidos") }
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
                        // Canchas queda como raíz tras salir de la bienvenida
                        popUpTo(Canchas) { saveState = true }
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
