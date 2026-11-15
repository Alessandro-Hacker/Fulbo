package pe.fulbo.app.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing

/** Iniciar sesión con correo y contraseña. */
@Composable
fun LoginScreen(
    estado: AuthUiState,
    onIniciarSesion: (correo: String, password: String) -> Unit,
    onCrearCuenta: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.screen)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(spacing.md))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(spacing.xl))
        FulboTextField(
            value = correo,
            onValueChange = { correo = it },
            label = "Correo",
            placeholder = "tucorreo@ejemplo.com",
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(spacing.md))
        FulboTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
        )

        MensajeError(estado.error)

        Spacer(Modifier.height(spacing.xl))
        FulboPrimaryButton(
            text = "Entrar",
            isLoading = estado.cargando,
            onClick = { onIniciarSesion(correo, password) },
        )
        TextButton(onClick = onCrearCuenta, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Crea una")
        }
    }
}
