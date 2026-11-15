package pe.fulbo.app.feature.equipo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.RankAvatar
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.RangoEquipo
import pe.fulbo.app.core.model.ResultadoPartidoItem

private val DIAS_SEMANA_ABREVIADOS: List<String> = listOf("L", "M", "X", "J", "V", "S", "D")
private const val DIA_SEMANA_JUGADO_DEFAULT: Int = 3 // Jueves
private const val SEMANAS_RACHA_ACTUALES: Int = 5
private const val SEMANAS_META_PROXIMO_RANGO: Int = 12

private val RESULTADOS_EJEMPLO: List<ResultadoPartidoItem> = listOf(
    ResultadoPartidoItem("1", "Los Pibes", 4, "Rayo FC", 2, "Ganó"),
    ResultadoPartidoItem("2", "Los Pibes", 1, "Deportivo", 1, "Empate"),
)

/**
 * Pantalla principal del equipo con racha semanal, rango y calendario (Mockup página 6).
 *
 * @param onRegistrarPartido Abre el marcador para registrar resultado.
 * @param onVerAlineacion Abre el tablero táctico.
 * @param onVerMarcos Abre la pantalla de gamificación de rangos.
 * @param onEditarEquipo Abre la edición y configuración del equipo.
 * @param modifier Modificador de Compose.
 */
@Composable
fun EquipoScreen(
    onRegistrarPartido: () -> Unit,
    onVerAlineacion: () -> Unit,
    onVerMarcos: () -> Unit,
    onEditarEquipo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FulboColors.Background),
        contentPadding = PaddingValues(horizontal = spacing.screen, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            EquipoCabecera(
                nombre = "Los Pibes del Jueves",
                deporteDetalle = "Fútbol · 7 vs 7",
                nombreRango = "Marco Plata",
                iniciales = "LP",
                rango = RangoEquipo.PLATA,
                onClickRango = onVerMarcos,
            )
        }

        item {
            RachaTarjetaSection(
                semanasActuales = SEMANAS_RACHA_ACTUALES,
                semanasMeta = SEMANAS_META_PROXIMO_RANGO,
                onClick = onVerMarcos,
            )
        }

        item {
            CalendarioSemanalSection(diaJugadoIndice = DIA_SEMANA_JUGADO_DEFAULT)
        }

        item {
            AccionesEquipoSection(
                onRegistrarPartido = onRegistrarPartido,
                onVerAlineacion = onVerAlineacion,
                onEditarEquipo = onEditarEquipo,
            )
        }

        item {
            UltimosResultadosSection(resultados = RESULTADOS_EJEMPLO)
        }
    }
}

@Composable
private fun EquipoCabecera(
    nombre: String,
    deporteDetalle: String,
    nombreRango: String,
    iniciales: String,
    rango: RangoEquipo,
    onClickRango: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(spacing.sm))
        RankAvatar(
            iniciales = iniciales,
            rango = rango,
            size = 80.dp,
            modifier = Modifier.clickable(onClick = onClickRango),
        )
        Spacer(Modifier.height(spacing.sm))

        Text(
            text = nombre,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClickRango),
        ) {
            Text(
                text = "$deporteDetalle · ",
                style = MaterialTheme.typography.bodyLarge,
                color = FulboColors.TextSecondary,
            )
            Text(
                text = nombreRango,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = FulboColors.Green,
            )
        }
    }
}

@Composable
private fun RachaTarjetaSection(
    semanasActuales: Int,
    semanasMeta: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val semanasRestantes = (semanasMeta - semanasActuales).coerceAtLeast(0)

    FulboCard(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(FulboColors.Orange.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = FulboColors.Orange,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Racha",
                        style = MaterialTheme.typography.labelMedium,
                        color = FulboColors.TextSecondary,
                    )
                    Text(
                        text = "Rango Plata",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                }
            }

            StatusPill(text = "$semanasActuales semanas seguidas", color = FulboColors.Orange, isFilled = false)
        }

        Spacer(Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { semanasActuales.toFloat() / semanasMeta.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = FulboColors.Orange,
            trackColor = FulboColors.SurfaceHigh,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Faltan $semanasRestantes semanas para el marco Oro",
            style = MaterialTheme.typography.bodySmall,
            color = FulboColors.TextSecondary,
        )
    }
}

@Composable
private fun CalendarioSemanalSection(
    diaJugadoIndice: Int,
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Text(
            text = "Esta semana",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DIAS_SEMANA_ABREVIADOS.forEachIndexed { index, dia ->
                val isJugado = index == diaJugadoIndice
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isJugado) FulboColors.Green else FulboColors.SurfaceHigh)
                        .border(1.dp, if (isJugado) FulboColors.Green else FulboColors.Outline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isJugado) FulboColors.OnGreen else FulboColors.TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccionesEquipoSection(
    onRegistrarPartido: () -> Unit,
    onVerAlineacion: () -> Unit,
    onEditarEquipo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FulboPrimaryButton(
            text = "Registrar partido de hoy",
            onClick = onRegistrarPartido,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            AccionEquipoChip(
                icono = Icons.Outlined.SportsSoccer,
                texto = "Alineación",
                onClick = onVerAlineacion,
                modifier = Modifier.weight(1f),
            )
            AccionEquipoChip(
                icono = Icons.Outlined.EmojiEvents,
                texto = "Resultado",
                onClick = onRegistrarPartido,
                modifier = Modifier.weight(1f),
            )
            AccionEquipoChip(
                icono = Icons.Outlined.Settings,
                texto = "Tu equipo",
                onClick = onEditarEquipo,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun UltimosResultadosSection(
    resultados: List<ResultadoPartidoItem>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Últimos resultados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Spacer(Modifier.height(spacing.sm))

        resultados.forEach { resultado ->
            FulboCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${resultado.local} ${resultado.golesLocal} – ${resultado.golesVisitante} ${resultado.visitante}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                    val pillColor = if (resultado.estado == "Ganó") FulboColors.Green else FulboColors.Orange
                    StatusPill(text = resultado.estado, color = pillColor, isFilled = true)
                }
            }
        }
    }
}

@Composable
private fun AccionEquipoChip(
    icono: ImageVector,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = FulboColors.Surface,
        border = BorderStroke(1.dp, FulboColors.Outline),
        modifier = modifier.height(48.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FulboColors.Green,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = FulboColors.TextPrimary,
            )
        }
    }
}
