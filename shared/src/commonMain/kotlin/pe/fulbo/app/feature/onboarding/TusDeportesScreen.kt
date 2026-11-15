package pe.fulbo.app.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.DeporteCasual

private const val MAXIMO_POSICIONES_SELECCIONABLES = 2

/**
 * Onboarding Paso 2 de 2: Tus deportes (Mockups páginas 11, 12 y 13).
 *
 * Permite configurar posiciones favoritas y nivel de habilidad para Fútbol, Básquet y Vóley.
 */
@Composable
fun TusDeportesScreen(
    onCrearPerfil: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var deporteActivo by remember { mutableStateOf(DeporteCasual.FUTBOL) }

    val mapaPosiciones = remember {
        mutableStateMapOf(
            DeporteCasual.FUTBOL to mutableSetOf("Mediocampista"),
            DeporteCasual.BASQUET to mutableSetOf("Escolta"),
            DeporteCasual.VOLEY to mutableSetOf("Armador", "Líbero"),
        )
    }

    val mapaNiveles = remember {
        mutableStateMapOf(
            DeporteCasual.FUTBOL to "Intermedio",
            DeporteCasual.BASQUET to "Intermedio",
            DeporteCasual.VOLEY to "Casual",
        )
    }

    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FulboColors.Background)
            .padding(spacing.screen)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(spacing.md))

        Text(
            text = "Paso 2 de 2",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.Green,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(spacing.xs))

        Text(
            text = "Tus deportes",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Spacer(Modifier.height(spacing.lg))

        // Selector superior de deportes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            DeporteCasual.entries.forEach { deporte ->
                val isDeporteActivo = deporte == deporteActivo
                FulboChip(
                    text = "${deporte.emoji} ${deporte.nombre}",
                    isSelected = isDeporteActivo,
                    onClick = { deporteActivo = deporte },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(spacing.lg))

        // Editor detallado del deporte activo
        DeporteActivoCard(
            deporte = deporteActivo,
            posicionesSeleccionadas = mapaPosiciones[deporteActivo] ?: emptySet(),
            nivelSeleccionado = mapaNiveles[deporteActivo] ?: "Casual",
            onPosicionToggle = { posicion ->
                val posicionesActuales = (mapaPosiciones[deporteActivo] ?: mutableSetOf()).toMutableSet()
                if (posicionesActuales.contains(posicion)) {
                    posicionesActuales.remove(posicion)
                } else if (posicionesActuales.size < MAXIMO_POSICIONES_SELECCIONABLES) {
                    posicionesActuales.add(posicion)
                }
                mapaPosiciones[deporteActivo] = posicionesActuales
            },
            onNivelSelected = { nuevoNivel ->
                mapaNiveles[deporteActivo] = nuevoNivel
            },
        )

        Spacer(Modifier.height(spacing.lg))

        // Resumen de los otros deportes
        DeporteCasual.entries
            .filter { it != deporteActivo }
            .forEach { otroDeporte ->
                val posiciones = mapaPosiciones[otroDeporte] ?: emptySet()
                val nivel = mapaNiveles[otroDeporte] ?: "Casual"

                DeporteResumenCard(
                    deporte = otroDeporte,
                    posiciones = posiciones,
                    nivel = nivel,
                    onClick = { deporteActivo = otroDeporte },
                    modifier = Modifier.padding(bottom = spacing.sm),
                )
            }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(spacing.xl))

        FulboPrimaryButton(
            text = "Crear mi perfil",
            onClick = onCrearPerfil,
        )
        Spacer(Modifier.height(spacing.md))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeporteActivoCard(
    deporte: DeporteCasual,
    posicionesSeleccionadas: Set<String>,
    nivelSeleccionado: String,
    onPosicionToggle: (String) -> Unit,
    onNivelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val posicionesDisponibles = DeporteCasual.obtenerPosiciones(deporte)

    Surface(
        shape = MaterialTheme.shapes.large,
        color = FulboColors.Surface,
        border = BorderStroke(1.dp, FulboColors.Outline),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = deporte.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FulboColors.TextPrimary,
                )
                StatusPill(text = "Editando", color = FulboColors.Green, isFilled = false)
            }

            Spacer(Modifier.height(spacing.md))

            Text(
                text = "Posiciones (elige hasta 2)",
                style = MaterialTheme.typography.labelLarge,
                color = FulboColors.TextSecondary,
            )
            Spacer(Modifier.height(spacing.sm))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                posicionesDisponibles.forEach { posicion ->
                    val isPosicionSeleccionada = posicionesSeleccionadas.contains(posicion)
                    FulboChip(
                        text = posicion,
                        isSelected = isPosicionSeleccionada,
                        onClick = { onPosicionToggle(posicion) },
                    )
                }
            }

            Spacer(Modifier.height(spacing.lg))

            Text(
                text = "Tu nivel (sé sincero)",
                style = MaterialTheme.typography.labelLarge,
                color = FulboColors.TextSecondary,
            )
            Spacer(Modifier.height(spacing.sm))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                DeporteCasual.niveles.forEach { nivel ->
                    val isNivelSeleccionado = nivel == nivelSeleccionado
                    FulboChip(
                        text = nivel,
                        isSelected = isNivelSeleccionado,
                        onClick = { onNivelSelected(nivel) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeporteResumenCard(
    deporte: DeporteCasual,
    posiciones: Set<String>,
    nivel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val isCompletado = posiciones.isNotEmpty()
    val estadoTexto = if (isCompletado) "Listo" else "Falta completar"
    val estadoColor = if (isCompletado) FulboColors.Green else FulboColors.Orange
    val descripcion = if (isCompletado) "${posiciones.joinToString(", ")} · $nivel" else "Toca para configurar"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FulboColors.Surface)
            .border(1.dp, FulboColors.Outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "${deporte.emoji} ${deporte.nombre}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FulboColors.TextPrimary,
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FulboColors.TextSecondary,
                )
            }
            StatusPill(text = estadoTexto, color = estadoColor)
        }
    }
}
