package pe.fulbo.app.feature.equipo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboDaySelector
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.component.RankAvatar
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.DeporteCasual
import pe.fulbo.app.core.model.RangoEquipo

private const val DIA_JUEVES_INDICE: Int = 3

/**
 * Pantalla de configuración y edición del equipo (Mockup página 8).
 *
 * @param onVolver Navegación hacia atrás.
 * @param onVerMarcos Navegación al catálogo de marcos y rangos.
 * @param onGuardarEquipo Callback para guardar los cambios del equipo.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEquipoScreen(
    onVolver: () -> Unit,
    onVerMarcos: () -> Unit,
    onGuardarEquipo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombreEquipo by remember { mutableStateOf("Los Pibes del Jueves") }
    var deporteSeleccionado by remember { mutableStateOf(DeporteCasual.FUTBOL) }
    var diasSeleccionados by remember { mutableStateOf(setOf(DIA_JUEVES_INDICE)) }

    val canGuardar = nombreEquipo.isNotBlank() && diasSeleccionados.isNotEmpty()
    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = FulboColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tu equipo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FulboColors.TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulboColors.Background),
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.screen)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(spacing.sm))

            AvatarEquipoHeader(
                iniciales = "LP",
                rango = RangoEquipo.PLATA,
                onVerMarcos = onVerMarcos,
            )

            Spacer(Modifier.height(spacing.lg))

            FulboTextField(
                value = nombreEquipo,
                onValueChange = { nombreEquipo = it },
                label = "Nombre del equipo",
                placeholder = "Ej: Los Pibes del Jueves",
            )

            Spacer(Modifier.height(spacing.md))

            SelectorDeporteEquipoRow(
                deporteSeleccionado = deporteSeleccionado,
                onDeporteSeleccionado = { deporteSeleccionado = it },
            )

            Spacer(Modifier.height(spacing.md))

            DiasJuegoSection(
                diasSeleccionados = diasSeleccionados,
                onToggleDia = { dia ->
                    diasSeleccionados = if (diasSeleccionados.contains(dia)) {
                        diasSeleccionados - dia
                    } else {
                        diasSeleccionados + dia
                    }
                },
            )

            Spacer(Modifier.height(spacing.lg))

            GamificacionInfoCard()

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(spacing.xl))

            FulboPrimaryButton(
                text = "Guardar equipo",
                isEnabled = canGuardar,
                onClick = onGuardarEquipo,
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun AvatarEquipoHeader(
    iniciales: String,
    rango: RangoEquipo,
    onVerMarcos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RankAvatar(
            iniciales = iniciales,
            rango = rango,
            size = 80.dp,
            modifier = Modifier.clickable(onClick = onVerMarcos),
        )

        TextButton(onClick = { /* Permite seleccionar foto de galería */ }) {
            Text("Cambiar foto", color = FulboColors.Green, fontWeight = FontWeight.SemiBold)
        }

        Surface(
            onClick = onVerMarcos,
            shape = CircleShape,
            color = FulboColors.Surface,
            border = BorderStroke(1.dp, FulboColors.Outline),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Marco Plata", color = FulboColors.TextPrimary, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Ver todos", color = FulboColors.Green, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun SelectorDeporteEquipoRow(
    deporteSeleccionado: DeporteCasual,
    onDeporteSeleccionado: (DeporteCasual) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Deporte",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            DeporteCasual.entries.forEach { deporte ->
                FulboChip(
                    text = "${deporte.emoji} ${deporte.nombre}",
                    isSelected = deporte == deporteSeleccionado,
                    onClick = { onDeporteSeleccionado(deporte) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DiasJuegoSection(
    diasSeleccionados: Set<Int>,
    onToggleDia: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "¿Qué días juegan?",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.sm))
        FulboDaySelector(
            selectedDays = diasSeleccionados,
            onDayToggle = onToggleDia,
        )
    }
}

@Composable
private fun GamificacionInfoCard(
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = FulboColors.Green,
                modifier = Modifier.padding(top = 2.dp),
            )
            Spacer(Modifier.padding(horizontal = 6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Si juegan cada semana, empieza la racha.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FulboColors.TextPrimary,
                )
                Text(
                    text = "El rango y el marco dependen del deporte del equipo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FulboColors.TextSecondary,
                )
            }
        }
    }
}
