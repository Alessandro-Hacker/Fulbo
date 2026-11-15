package pe.fulbo.app.feature.canchas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboSecondaryButton
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.CanchaMapaItem

private val COLOR_FONDO_MAPA: Color = Color(0xFF0F151D)
private val COLOR_PARQUE_MAPA: Color = Color(0xFF14291E)
private val COLOR_RIO_MAPA: Color = Color(0xFF122234)
private val COLOR_CALLE_MAPA: Color = Color(0xFF1D2633)
private val COLOR_AVENIDA_MAPA: Color = Color(0xFF263344)

private val CANCHAS_INICIALES: List<CanchaMapaItem> = listOf(
    CanchaMapaItem(
        id = "1",
        nombre = "Cancha Los Álamos",
        direccion = "Av. Los Álamos 450",
        horario = "Hasta las 11:00 pm",
        distancia = "a 1.2 km",
        isAbierto = true,
        latRelativa = 0.52f,
        lngRelativa = 0.48f,
    ),
    CanchaMapaItem(
        id = "2",
        nombre = "Losa Deportiva Miraflores",
        direccion = "Calle Las Flores 120",
        horario = "Hasta las 10:00 pm",
        distancia = "a 2.4 km",
        isAbierto = true,
        latRelativa = 0.35f,
        lngRelativa = 0.65f,
    ),
    CanchaMapaItem(
        id = "3",
        nombre = "Cancha San Eduardo",
        direccion = "Urb. San Eduardo Mz. B",
        horario = "Hasta las 12:00 am",
        distancia = "a 3.1 km",
        isAbierto = true,
        latRelativa = 0.68f,
        lngRelativa = 0.28f,
    ),
)

/**
 * Pantalla de búsqueda de canchas con mapa interactivo y tarjeta inferior flotante (Mockup página 5).
 *
 * @param onCrearPartidoAqui Acción invocada para abrir la publicación prellenada con la cancha elegida.
 * @param modifier Modificador de Compose.
 */
@Composable
fun MapaScreen(
    onCrearPartidoAqui: (nombreCancha: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var busqueda by remember { mutableStateOf("") }
    var isFiltroAbiertasAhoraActivo by remember { mutableStateOf(true) }
    var isFiltroCercaDeMiActivo by remember { mutableStateOf(true) }

    val canchas = remember { CANCHAS_INICIALES }
    var canchaSeleccionada by remember { mutableStateOf(canchas.first()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FulboColors.Background),
    ) {
        MapaCanvas(modifier = Modifier.fillMaxSize())

        canchas.forEach { cancha ->
            val isCanchaSeleccionada = cancha.id == canchaSeleccionada.id
            CanchaPinMarcador(
                cancha = cancha,
                isSeleccionada = isCanchaSeleccionada,
                onClick = { canchaSeleccionada = cancha },
            )
        }

        MapaBuscadorRow(
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it },
            isFiltroAbiertasAhoraActivo = isFiltroAbiertasAhoraActivo,
            onToggleAbiertasAhora = { isFiltroAbiertasAhoraActivo = !isFiltroAbiertasAhoraActivo },
            isFiltroCercaDeMiActivo = isFiltroCercaDeMiActivo,
            onToggleCercaDeMi = { isFiltroCercaDeMiActivo = !isFiltroCercaDeMiActivo },
        )

        CanchaDetalleCard(
            cancha = canchaSeleccionada,
            onCrearPartido = { onCrearPartidoAqui(canchaSeleccionada.nombre) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MapaCanvas(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val ancho = size.width
        val alto = size.height

        drawRect(COLOR_FONDO_MAPA)

        // Áreas verdes / parques
        drawCircle(COLOR_PARQUE_MAPA, radius = ancho * 0.35f, center = Offset(ancho * 0.2f, alto * 0.4f))
        drawCircle(COLOR_PARQUE_MAPA, radius = ancho * 0.25f, center = Offset(ancho * 0.8f, alto * 0.7f))

        // Canal / río
        drawLine(
            color = COLOR_RIO_MAPA,
            start = Offset(0f, alto * 0.25f),
            end = Offset(ancho, alto * 0.32f),
            strokeWidth = 24.dp.toPx(),
        )

        // Cuadrícula de avenidas y calles
        for (indiceHorizontal in 1..8) {
            val isAvenida = indiceHorizontal % 3 == 0
            drawLine(
                color = if (isAvenida) COLOR_AVENIDA_MAPA else COLOR_CALLE_MAPA,
                start = Offset(0f, alto * (indiceHorizontal * 0.11f)),
                end = Offset(ancho, alto * (indiceHorizontal * 0.11f) + (indiceHorizontal * 5)),
                strokeWidth = if (isAvenida) 5.dp.toPx() else 2.5.dp.toPx(),
            )
        }

        for (indiceVertical in 1..6) {
            val isAvenida = indiceVertical % 2 == 0
            drawLine(
                color = if (isAvenida) COLOR_AVENIDA_MAPA else COLOR_CALLE_MAPA,
                start = Offset(ancho * (indiceVertical * 0.16f), 0f),
                end = Offset(ancho * (indiceVertical * 0.16f) + 20, alto),
                strokeWidth = if (isAvenida) 5.dp.toPx() else 2.5.dp.toPx(),
            )
        }
    }
}

@Composable
private fun CanchaPinMarcador(
    cancha: CanchaMapaItem,
    isSeleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 220.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = (320 * cancha.lngRelativa).dp,
                    y = (450 * cancha.latRelativa).dp,
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isSeleccionada) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(FulboColors.Green.copy(alpha = 0.25f), CircleShape),
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(6.dp, CircleShape)
                    .background(if (isSeleccionada) FulboColors.Green else FulboColors.Surface, CircleShape)
                    .border(2.dp, if (isSeleccionada) FulboColors.OnGreen else FulboColors.Outline, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = cancha.nombre,
                    tint = if (isSeleccionada) FulboColors.OnGreen else FulboColors.Green,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun MapaBuscadorRow(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    isFiltroAbiertasAhoraActivo: Boolean,
    onToggleAbiertasAhora: () -> Unit,
    isFiltroCercaDeMiActivo: Boolean,
    onToggleCercaDeMi: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screen, vertical = spacing.md),
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            placeholder = { Text("Buscar canchas", color = FulboColors.TextDisabled) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Buscar", tint = FulboColors.TextSecondary)
            },
            singleLine = true,
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FulboColors.Surface,
                unfocusedContainerColor = FulboColors.Surface,
                focusedBorderColor = FulboColors.Green,
                unfocusedBorderColor = FulboColors.Outline,
                focusedTextColor = FulboColors.TextPrimary,
                unfocusedTextColor = FulboColors.TextPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, CircleShape),
        )

        Spacer(Modifier.height(spacing.sm))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            FulboChip(
                text = "Abiertas ahora",
                isSelected = isFiltroAbiertasAhoraActivo,
                onClick = onToggleAbiertasAhora,
            )
            FulboChip(
                text = "Cerca de mí",
                isSelected = isFiltroCercaDeMiActivo,
                onClick = onToggleCercaDeMi,
            )
        }
    }
}

@Composable
private fun CanchaDetalleCard(
    cancha: CanchaMapaItem,
    onCrearPartido: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = FulboColors.Surface,
        border = BorderStroke(1.dp, FulboColors.Outline),
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.screen),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(FulboColors.Outline),
            )

            Spacer(Modifier.height(spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = cancha.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FulboColors.TextPrimary,
                )
                StatusPill(
                    text = if (cancha.isAbierto) "Abierto" else "Cerrado",
                    color = if (cancha.isAbierto) FulboColors.Green else FulboColors.Red,
                    isFilled = true,
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${cancha.horario} · ${cancha.distancia}",
                style = MaterialTheme.typography.bodyMedium,
                color = FulboColors.TextSecondary,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Datos de Google Maps",
                style = MaterialTheme.typography.bodySmall,
                color = FulboColors.TextDisabled,
            )

            Spacer(Modifier.height(spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                FulboSecondaryButton(
                    text = "Cómo llegar",
                    onClick = { /* Abre mapas externos o direcciones */ },
                    modifier = Modifier.weight(1f),
                )

                FulboPrimaryButton(
                    text = "Crear partido aquí",
                    onClick = onCrearPartido,
                    modifier = Modifier.weight(1.3f),
                )
            }
        }
    }
}
