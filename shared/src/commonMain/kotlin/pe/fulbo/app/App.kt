package pe.fulbo.app

import androidx.compose.runtime.Composable
import pe.fulbo.app.core.designsystem.theme.FulboTheme
import pe.fulbo.app.navigation.FulboNavHost

/** Raíz de la UI. Android la llama desde MainActivity; iOS desde MainViewController. */
@Composable
fun App() {
    FulboTheme {
        FulboNavHost()
    }
}
