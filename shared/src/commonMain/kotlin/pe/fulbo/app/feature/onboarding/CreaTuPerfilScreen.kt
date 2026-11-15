package pe.fulbo.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing

/**
 * Onboarding Paso 1 de 2: Crea tu perfil (Mockup página 1).
 *
 * Permite registrar foto de perfil, nombre, apellido y alias antes de seleccionar deportes.
 */
@Composable
fun CreaTuPerfilScreen(
    onSiguiente: (nombre: String, apellido: String, alias: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf("Diego") }
    var apellido by remember { mutableStateOf("Ramírez") }
    var alias by remember { mutableStateOf("Chino") }
    var hasFotoSeleccionada by remember { mutableStateOf(false) }

    val spacing = MaterialTheme.spacing
    val canContinuar = nombre.isNotBlank() && apellido.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FulboColors.Background)
            .padding(spacing.screen)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(spacing.md))

        Text(
            text = "Paso 1 de 2",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.Green,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(spacing.xs))

        Text(
            text = "Crea tu perfil",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(spacing.xs))

        Text(
            text = "Para que otros sepan con quién van a jugar.",
            style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary,
        )

        Spacer(Modifier.height(spacing.xl))

        // Selector circular "Subir foto"
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                val bordeColor = if (hasFotoSeleccionada) FulboColors.Green else FulboColors.Outline
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(FulboColors.Surface)
                        .border(1.5.dp, bordeColor, CircleShape)
                        .clickable { hasFotoSeleccionada = !hasFotoSeleccionada },
                    contentAlignment = Alignment.Center,
                ) {
                    if (hasFotoSeleccionada) {
                        Text(
                            text = "DR",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = FulboColors.Green,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Subir foto",
                            tint = FulboColors.TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
                Text(
                    text = if (hasFotoSeleccionada) "Foto seleccionada" else "Subir foto",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hasFotoSeleccionada) FulboColors.Green else FulboColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(spacing.xl))

        // Campos de texto para datos personales
        FulboTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre",
            placeholder = "Tu nombre",
        )
        Spacer(Modifier.height(spacing.md))

        FulboTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = "Apellido",
            placeholder = "Tu apellido",
        )
        Spacer(Modifier.height(spacing.md))

        FulboTextField(
            value = alias,
            onValueChange = { alias = it },
            label = "Alias (opcional)",
            placeholder = "Ej: Chino, Lucho, Rayo...",
            supportingText = "Tu alias es el nombre que verán los demás en los partidos.",
        )

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(spacing.xl))

        FulboPrimaryButton(
            text = "Siguiente",
            isEnabled = canContinuar,
            onClick = { onSiguiente(nombre, apellido, alias) },
        )
        Spacer(Modifier.height(spacing.md))
    }
}
