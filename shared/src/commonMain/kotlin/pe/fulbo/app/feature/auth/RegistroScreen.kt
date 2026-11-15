package pe.fulbo.app.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing

/** Crear la cuenta de acceso (correo y contraseña). Después sigue "Crea tu perfil". */
@Composable
fun RegistroScreen(
    estado: AuthUiState,
    onCrearCuenta: (correo: String, password: String, mayorDe16: Boolean) -> Unit,
    onYaTengoCuenta: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mayorDe16 by remember { mutableStateOf(false) }
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.screen)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(spacing.md))
        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = "Con tu correo y una contraseña para entrar a Fulbo.",
            style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary,
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
            placeholder = "Mínimo 8 caracteres, con letras y números",
            isPassword = true,
        )

        Spacer(Modifier.height(spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth().clickable { mayorDe16 = !mayorDe16 },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = mayorDe16,
                onCheckedChange = { mayorDe16 = it },
                colors = CheckboxDefaults.colors(checkedColor = FulboColors.Green),
            )
            Text(
                text = "Tengo 16 años o más",
                style = MaterialTheme.typography.bodyMedium,
                color = FulboColors.TextPrimary,
            )
        }

        MensajeError(estado.error)

        Spacer(Modifier.height(spacing.xl))
        FulboPrimaryButton(
            text = "Crear cuenta",
            isLoading = estado.cargando,
            onClick = { onCrearCuenta(correo, password, mayorDe16) },
        )
        TextButton(onClick = onYaTengoCuenta, modifier = Modifier.fillMaxWidth()) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

@Composable
internal fun MensajeError(mensaje: String?) {
    if (mensaje == null) return
    Text(
        text = mensaje,
        style = MaterialTheme.typography.bodyMedium,
        color = FulboColors.Orange,
        modifier = Modifier.padding(top = MaterialTheme.spacing.md),
    )
}
