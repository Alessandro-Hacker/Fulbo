package pe.fulbo.app.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboSecondaryButton
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.FulboTheme
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.resources.Res
import pe.fulbo.app.resources.welcome_create_account
import pe.fulbo.app.resources.welcome_google
import pe.fulbo.app.resources.welcome_have_account
import pe.fulbo.app.resources.welcome_login
import pe.fulbo.app.resources.welcome_phone
import pe.fulbo.app.resources.welcome_subtitle
import pe.fulbo.app.resources.welcome_title

/** Mockup página 1. La lógica de cada botón se implementa en las issues de M1. */
@Composable
fun WelcomeScreen(
    onCrearCuenta: () -> Unit,
    onGoogle: () -> Unit,
    onNumero: () -> Unit,
    onIniciarSesion: () -> Unit,
) {
    val sp = MaterialTheme.spacing
    Column(
        Modifier.fillMaxSize().padding(sp.screen),
        verticalArrangement = Arrangement.spacedBy(sp.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.SportsSoccer, null, tint = FulboColors.Green, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Text("FULBO", fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        }
        Spacer(Modifier.height(sp.sm))

        // Tarjeta de partido de ejemplo (en el mockup va sobre un dibujo de cancha)
        FulboCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SÁB", style = MaterialTheme.typography.labelSmall, color = FulboColors.TextSecondary)
                    Text("08", style = MaterialTheme.typography.titleLarge)
                }
                Column(Modifier.weight(1f)) {
                    Text("Fulbito 7v7 · 20:00", style = MaterialTheme.typography.titleMedium)
                    Text("Complejo San Borja · faltan 2 jugadores", style = MaterialTheme.typography.bodyMedium,
                        color = FulboColors.TextSecondary)
                }
                StatusPill("Abierto", FulboColors.Green)
            }
        }

        Spacer(Modifier.height(sp.sm))
        Text(stringResource(Res.string.welcome_title), style = MaterialTheme.typography.displaySmall)
        Text(stringResource(Res.string.welcome_subtitle), style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary)

        Spacer(Modifier.weight(1f))
        FulboPrimaryButton(stringResource(Res.string.welcome_create_account), onCrearCuenta)
        FulboSecondaryButton(stringResource(Res.string.welcome_google), onGoogle)
        FulboSecondaryButton(stringResource(Res.string.welcome_phone), onNumero)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.welcome_have_account), color = FulboColors.TextSecondary)
            TextButton(onClick = onIniciarSesion) { Text(stringResource(Res.string.welcome_login)) }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F14, heightDp = 800)
@Composable
private fun WelcomePreview() = FulboTheme { WelcomeScreen({}, {}, {}, {}) }
