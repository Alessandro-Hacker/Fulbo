package pe.fulbo.app.feature.canchas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.FulboTheme
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.resources.Res
import pe.fulbo.app.resources.canchas_count
import pe.fulbo.app.resources.canchas_search_hint
import pe.fulbo.app.resources.error_generic
import pe.fulbo.app.resources.error_no_connection
import pe.fulbo.app.resources.error_retry
import pe.fulbo.app.resources.per_hour
import pe.fulbo.app.feature.canchas.domain.Cancha

/** Punto de entrada desde la navegación: conecta el ViewModel con la UI. */
@Composable
fun CanchasRoute(
    onCanchaClick: (String) -> Unit,
    viewModel: CanchasViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CanchasScreen(
        state = state,
        onBusquedaChange = viewModel::onBusquedaChange,
        onFiltroClick = viewModel::onFiltroClick,
        onReintentar = { viewModel.refrescar() },
        onCanchaClick = onCanchaClick,
    )
}

/** UI pura (sin ViewModel) → fácil de previsualizar y testear. Mockup página 2. */
@Composable
fun CanchasScreen(
    state: CanchasUiState,
    onBusquedaChange: (String) -> Unit,
    onFiltroClick: (FiltroCancha) -> Unit,
    onReintentar: () -> Unit,
    onCanchaClick: (String) -> Unit,
) {
    val sp = MaterialTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(sp.screen),
        verticalArrangement = Arrangement.spacedBy(sp.md),
    ) {
        item {
            OutlinedTextField(
                value = state.busqueda,
                onValueChange = onBusquedaChange,
                placeholder = { Text(stringResource(Res.string.canchas_search_hint)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = FulboColors.Surface,
                    focusedContainerColor = FulboColors.Surface,
                    unfocusedBorderColor = FulboColors.Outline,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(sp.sm)) {
                items(FiltroCancha.entries) { f ->
                    FulboChip(f.etiqueta, selected = f in state.filtros, onClick = { onFiltroClick(f) })
                }
            }
        }
        item {
            // TODO(M2 Google Maps): reemplazar por GoogleMap con marcadores de precio
            Box(
                Modifier.fillMaxWidth().height(220.dp)
                    .background(FulboColors.Surface, MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center,
            ) { Text("[MAPA]", color = FulboColors.TextSecondary) }
        }
        item {
            Text(
                stringResource(Res.string.canchas_count, state.canchas.size),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        val error = state.error
        when {
            error != null -> item { ErrorBox(error, onReintentar) }
            state.cargando && state.canchas.isEmpty() -> item {
                Box(Modifier.fillMaxWidth().padding(sp.lg), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> items(state.canchas, key = { it.id }) { cancha ->
                CanchaCard(cancha, onClick = { onCanchaClick(cancha.id) })
            }
        }
    }
}

@Composable
private fun CanchaCard(cancha: Cancha, onClick: () -> Unit) {
    FulboCard(onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                Modifier.size(76.dp).background(FulboColors.SurfaceHigh, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) { Text("[FOTO]", style = MaterialTheme.typography.labelSmall, color = FulboColors.TextSecondary) }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cancha.nombre, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, null, tint = FulboColors.Green, modifier = Modifier.size(14.dp))
                    Text(" ${cancha.rating}", color = FulboColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    listOf(cancha.superficie, cancha.formato, if (cancha.techada) "Techada" else "Al aire libre",
                        cancha.distanciaTexto).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium, color = FulboColors.TextSecondary,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    cancha.proximoLibre?.let {
                        StatusPill(it, if (cancha.ultimaHora) FulboColors.Orange else FulboColors.Green, filled = false)
                    }
                    Text("S/ ${cancha.precioHora.toInt()}${stringResource(Res.string.per_hour)}",
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun ErrorBox(error: AppError, onReintentar: () -> Unit) {
    val msg = if (error is AppError.SinConexion) stringResource(Res.string.error_no_connection)
    else stringResource(Res.string.error_generic)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(msg, color = FulboColors.TextSecondary)
        TextButton(onClick = onReintentar) { Text(stringResource(Res.string.error_retry)) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F14, heightDp = 800)
@Composable
private fun CanchasPreview() = FulboTheme {
    CanchasScreen(
        state = CanchasUiState(
            cargando = false,
            canchas = listOf(
                Cancha("1", "La Bombonerita", "Miraflores", "Sintético", "7v7", true, 4.8, 120.0, 650, "Libre 19:00", false),
                Cancha("2", "Surquillo Pro", "Surquillo", "Sintético", "5v5", false, 4.5, 95.0, 1200, "Última hora 21:00", true),
            ),
        ),
        onBusquedaChange = {}, onFiltroClick = {}, onReintentar = {}, onCanchaClick = {},
    )
}
