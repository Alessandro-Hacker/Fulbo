package pe.fulbo.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.model.Posicion
import pe.fulbo.app.core.model.RangoEquipo

private const val COLOR_BRONCE = 0xFFCD7F32
private const val COLOR_ORO_PRINCIPAL = 0xFFFFD700
private const val COLOR_ORO_FONDO = 0xFFF59E0B
private const val COLOR_ORO_BRILLO = 0xFFFFFBEB
private const val COLOR_PLATINO_CYAN = 0xFF00E5FF
private const val COLOR_PLATINO_PURPURA = 0xFFA855F7
private const val COLOR_PLATINO_ROSA = 0xFFEC4899
private const val COLOR_FUEGO_ROJO = 0xFFFF3D00
private const val COLOR_FUEGO_NARANJA = 0xFFFF9100
private const val COLOR_FUEGO_AMARILLO = 0xFFFFEA00

/**
 * Botón verde principal de acción destacada (Crear mi cuenta, Reservar, Publicar partido).
 */
@Composable
fun FulboPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = FulboColors.Green,
            contentColor = FulboColors.OnGreen,
            disabledContainerColor = FulboColors.SurfaceHigh,
            disabledContentColor = FulboColors.TextDisabled,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = FulboColors.OnGreen,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * Botón secundario con borde sutil para acciones alternativas (Continuar con Google, Cómo llegar).
 */
@Composable
fun FulboSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, FulboColors.Outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = FulboColors.TextPrimary),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Chip de selección o filtro rápido.
 */
@Composable
fun FulboChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val colorFondo = if (isSelected) FulboColors.Green else FulboColors.Surface
    val colorTexto = when {
        !isEnabled -> FulboColors.TextDisabled
        isSelected -> FulboColors.OnGreen
        else -> FulboColors.TextPrimary
    }

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        shape = CircleShape,
        color = colorFondo,
        contentColor = colorTexto,
        border = if (isSelected) null else BorderStroke(1.dp, FulboColors.Outline),
        modifier = modifier.height(38.dp),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

/**
 * Tarjeta base del sistema de diseño con elevación sutil y bordes redondeados.
 */
@Composable
fun FulboCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val forma = MaterialTheme.shapes.large
    val borde = BorderStroke(1.dp, FulboColors.Outline)

    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = forma,
            color = FulboColors.Surface,
            border = borde,
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    } else {
        Surface(
            shape = forma,
            color = FulboColors.Surface,
            border = borde,
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    }
}

/**
 * Indicador de píldora de estado (Abierto, Falta 1 jugador, etc.).
 */
@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    isFilled: Boolean = false,
) {
    val fondoColor = if (isFilled) color else color.copy(alpha = 0.15f)
    val textoColor = if (isFilled) FulboColors.OnGreen else color

    Box(
        modifier = modifier
            .background(fondoColor, CircleShape)
            .border(1.dp, color.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = textoColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Etiqueta de posición según el color del rol.
 */
@Composable
fun PosicionTag(posicion: Posicion, modifier: Modifier = Modifier) {
    val color = when (posicion) {
        Posicion.ARQUERO -> FulboColors.Orange
        Posicion.DEFENSA -> FulboColors.Blue
        Posicion.MEDIOCAMPO -> FulboColors.Green
        Posicion.DELANTERO -> FulboColors.Purple
    }
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(posicion.sigla, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

/**
 * Avatar circular de jugador que muestra sus iniciales.
 */
@Composable
fun PlayerAvatar(
    nombre: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    ringColor: Color? = null,
) {
    val iniciales = nombre.split(" ", ".")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val bordeModifier = if (ringColor != null) {
        Modifier.border(1.5.dp, ringColor, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .background(FulboColors.SurfaceHigh, CircleShape)
            .then(bordeModifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = iniciales,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Avatar circular con marco de rango (Bronce, Plata, Oro, Platino, Leyenda).
 */
@Composable
fun RankAvatar(
    iniciales: String,
    rango: RangoEquipo,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val (bordeModifier, sombraModifier) = when (rango) {
        RangoEquipo.BRONCE -> Pair(
            Modifier.border(2.dp, Color(COLOR_BRONCE), CircleShape),
            Modifier,
        )
        RangoEquipo.PLATA -> {
            val silverBrush = Brush.sweepGradient(
                listOf(
                    Color(0xFFE2E8F0),
                    Color(0xFF94A3B8),
                    Color(0xFFFFFFFF),
                    Color(0xFF94A3B8),
                    Color(0xFFE2E8F0),
                ),
            )
            Pair(
                Modifier.border(3.dp, silverBrush, CircleShape),
                Modifier.shadow(4.dp, CircleShape, spotColor = Color(0xFFE2E8F0), ambientColor = Color(0xFF94A3B8)),
            )
        }
        RangoEquipo.ORO -> {
            val goldBrush = Brush.sweepGradient(
                listOf(
                    Color(COLOR_ORO_PRINCIPAL),
                    Color(COLOR_ORO_FONDO),
                    Color(COLOR_ORO_BRILLO),
                    Color(COLOR_ORO_FONDO),
                    Color(COLOR_ORO_PRINCIPAL),
                ),
            )
            Pair(
                Modifier.border(3.5.dp, goldBrush, CircleShape),
                Modifier.shadow(8.dp, CircleShape, spotColor = Color(COLOR_ORO_PRINCIPAL), ambientColor = Color(COLOR_ORO_FONDO)),
            )
        }
        RangoEquipo.PLATINO -> {
            val platinoBrush = Brush.sweepGradient(
                listOf(
                    Color(COLOR_PLATINO_CYAN),
                    Color(COLOR_PLATINO_PURPURA),
                    Color(COLOR_PLATINO_ROSA),
                    Color(COLOR_PLATINO_CYAN),
                ),
            )
            Pair(
                Modifier.border(4.dp, platinoBrush, CircleShape),
                Modifier.shadow(10.dp, CircleShape, spotColor = Color(COLOR_PLATINO_CYAN), ambientColor = Color(COLOR_PLATINO_PURPURA)),
            )
        }
        RangoEquipo.LEYENDA -> {
            val fuegoBrush = Brush.sweepGradient(
                listOf(
                    Color(COLOR_FUEGO_ROJO),
                    Color(COLOR_FUEGO_NARANJA),
                    Color(COLOR_FUEGO_AMARILLO),
                    Color(COLOR_FUEGO_ROJO),
                ),
            )
            Pair(
                Modifier.border(4.5.dp, fuegoBrush, CircleShape),
                Modifier.shadow(14.dp, CircleShape, spotColor = Color(COLOR_FUEGO_ROJO), ambientColor = Color(COLOR_FUEGO_NARANJA)),
            )
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .then(sombraModifier)
            .clip(CircleShape)
            .background(FulboColors.SurfaceHigh)
            .then(bordeModifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = iniciales,
            style = if (size > 50.dp) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Campo de texto estilizado para formularios con diseño consistente de Fulbo.
 */
@Composable
fun FulboTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    supportingText: String? = null,
    isSingleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = FulboColors.TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            ),
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = FulboColors.TextDisabled) }
            } else null,
            trailingIcon = trailingIcon,
            singleLine = isSingleLine,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FulboColors.Surface,
                unfocusedContainerColor = FulboColors.Surface,
                focusedBorderColor = FulboColors.Green,
                unfocusedBorderColor = FulboColors.Outline,
                focusedTextColor = FulboColors.TextPrimary,
                unfocusedTextColor = FulboColors.TextPrimary,
                cursorColor = FulboColors.Green,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = FulboColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

/**
 * Control stepper numérico para incremento y decremento de valores enteros.
 */
@Composable
fun FulboStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minimo: Int = 1,
    maximo: Int = 22,
) {
    val canDecrementar = value > minimo
    val canIncrementar = value < maximo

    Row(
        modifier = modifier
            .background(FulboColors.Surface, RoundedCornerShape(12.dp))
            .border(1.dp, FulboColors.Outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(
            onClick = { if (canDecrementar) onValueChange(value - 1) },
            enabled = canDecrementar,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Menos",
                tint = if (canDecrementar) FulboColors.TextPrimary else FulboColors.TextDisabled,
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
        )
        IconButton(
            onClick = { if (canIncrementar) onValueChange(value + 1) },
            enabled = canIncrementar,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Más",
                tint = if (canIncrementar) FulboColors.TextPrimary else FulboColors.TextDisabled,
            )
        }
    }
}

/**
 * Selector interactivo de los días de la semana con botones circulares.
 */
@Composable
fun FulboDaySelector(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val etiquetasDias = listOf("L", "M", "X", "J", "V", "S", "D")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        etiquetasDias.forEachIndexed { indice, etiqueta ->
            val isDiaSeleccionado = selectedDays.contains(indice)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDiaSeleccionado) FulboColors.Green else FulboColors.Surface)
                    .border(
                        1.dp,
                        if (isDiaSeleccionado) FulboColors.Green else FulboColors.Outline,
                        CircleShape,
                    )
                    .clickable { onDayToggle(indice) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDiaSeleccionado) FulboColors.OnGreen else FulboColors.TextPrimary,
                )
            }
        }
    }
}

/**
 * Fila de estrellas para representar calificaciones y puntuación de reputación.
 */
@Composable
fun StarRatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (indice in 1..maxStars) {
            val colorEstrella = if (indice <= rating) Color(COLOR_ORO_PRINCIPAL) else FulboColors.TextDisabled
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = colorEstrella,
                modifier = Modifier.size(starSize),
            )
        }
    }
}
