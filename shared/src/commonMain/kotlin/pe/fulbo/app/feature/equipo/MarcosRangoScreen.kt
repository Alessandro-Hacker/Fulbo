package pe.fulbo.app.feature.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import pe.fulbo.app.core.designsystem.component.RankAvatar
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.DeporteCasual
import pe.fulbo.app.core.model.RangoEquipo

/**
 * Pantalla informativa y visual del catálogo de Marcos por Rango (Mockup página 9).
 * Muestra el sistema de gamificación semanal para los equipos.
 *
 * @param onVolver Callback de retorno.
 * @param rangoActual Rango activo del equipo para destacarlo en la lista.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcosRangoScreen(
    onVolver: () -> Unit,
    rangoActual: RangoEquipo = RangoEquipo.PLATA,
    modifier: Modifier = Modifier,
) {
    var deporteSeleccionado by remember { mutableStateOf(DeporteCasual.FUTBOL) }
    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = FulboColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Marcos por rango",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = spacing.screen, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            item {
                MarcosRangoHeader(
                    deporteSeleccionado = deporteSeleccionado,
                    onDeporteSeleccionado = { deporteSeleccionado = it },
                )
            }

            items(RangoEquipo.entries, key = { it.id }) { rango ->
                val isEquipoActual = rango == rangoActual
                RangoTarjetaItem(
                    rango = rango,
                    isEquipoActual = isEquipoActual,
                )
            }
        }
    }
}

@Composable
private fun MarcosRangoHeader(
    deporteSeleccionado: DeporteCasual,
    onDeporteSeleccionado: (DeporteCasual) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "El rango se cuenta por deporte: cada equipo sube jugando cada semana.",
            style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.sm))

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
private fun RangoTarjetaItem(
    rango: RangoEquipo,
    isEquipoActual: Boolean,
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RankAvatar(
                iniciales = "LP",
                rango = rango,
                size = 58.dp,
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = rango.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                    if (isEquipoActual) {
                        StatusPill(text = "Tu equipo", color = FulboColors.Green, isFilled = true)
                    }
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = rango.semanas,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FulboColors.TextSecondary,
                )

                Text(
                    text = rango.descripcionAro,
                    style = MaterialTheme.typography.bodySmall,
                    color = FulboColors.TextDisabled,
                )
            }
        }
    }
}
