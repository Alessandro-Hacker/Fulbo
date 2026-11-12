package pe.fulbo.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.FulboTheme
import pe.fulbo.app.core.model.Posicion

/** Botón verde principal (Crear mi cuenta, Reservar, Publicar partido…). */
@Composable
fun FulboPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = FulboColors.Green,
            contentColor = FulboColors.OnGreen,
            disabledContainerColor = FulboColors.SurfaceHigh,
            disabledContentColor = FulboColors.TextDisabled,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(22.dp), color = FulboColors.OnGreen, strokeWidth = 2.dp)
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Botón con borde (Continuar con Google, Rebalancear…). */
@Composable
fun FulboSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, FulboColors.Outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = FulboColors.TextPrimary),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Chip de filtro o selección (Abierto ahora, 7v7, Intermedio, horarios…). */
@Composable
fun FulboChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val bg = if (selected) FulboColors.Green else FulboColors.Surface
    val fg = when {
        !enabled -> FulboColors.TextDisabled
        selected -> FulboColors.OnGreen
        else -> FulboColors.TextPrimary
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = bg,
        contentColor = fg,
        border = if (selected) null else BorderStroke(1.dp, FulboColors.Outline),
        modifier = modifier.height(40.dp),
    ) {
        Box(Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Tarjeta base con fondo de superficie y borde sutil. */
@Composable
fun FulboCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    val inner: @Composable () -> Unit = { Column(Modifier.padding(contentPadding), content = content) }
    if (onClick != null) {
        Surface(onClick = onClick, shape = shape, color = FulboColors.Surface,
            border = BorderStroke(1.dp, FulboColors.Outline), modifier = modifier.fillMaxWidth()) { inner() }
    } else {
        Surface(shape = shape, color = FulboColors.Surface,
            border = BorderStroke(1.dp, FulboColors.Outline), modifier = modifier.fillMaxWidth()) { inner() }
    }
}

/** Píldora de estado: "Abierto", "Faltan 2", "Completo", "2 pendientes". */
@Composable
fun StatusPill(text: String, color: Color, modifier: Modifier = Modifier, filled: Boolean = true) {
    Box(
        modifier
            .background(if (filled) color else color.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = if (filled) FulboColors.OnGreen else color,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/** Etiqueta de posición con su color: ARQ naranja, DEF azul, MED verde, DEL morado. */
@Composable
fun PosicionTag(posicion: Posicion, modifier: Modifier = Modifier) {
    val color = when (posicion) {
        Posicion.ARQUERO -> FulboColors.Orange
        Posicion.DEFENSA -> FulboColors.Blue
        Posicion.MEDIOCAMPO -> FulboColors.Green
        Posicion.DELANTERO -> FulboColors.Purple
    }
    Box(
        modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(posicion.sigla, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

/** Avatar circular con iniciales (se reemplazará por foto cuando exista). */
@Composable
fun PlayerAvatar(nombre: String, modifier: Modifier = Modifier, size: Dp = 40.dp, ringColor: Color? = null) {
    val iniciales = nombre.split(" ", ".").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
    Box(
        modifier
            .size(size)
            .background(FulboColors.SurfaceHigh, CircleShape)
            .then(if (ringColor != null) Modifier.border(1.5.dp, ringColor, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(iniciales, style = MaterialTheme.typography.labelMedium, color = FulboColors.TextPrimary,
            textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F14)
@Composable
private fun ComponentsPreview() {
    FulboTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FulboPrimaryButton("Crear mi cuenta", onClick = {})
            FulboSecondaryButton("Continuar con Google", onClick = {})
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FulboChip("Abierto ahora", selected = true, onClick = {})
                FulboChip("Fútbol 7", selected = false, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusPill("Abierto", FulboColors.Green)
                StatusPill("Faltan 2", FulboColors.Orange, filled = false)
                Posicion.entries.forEach { PosicionTag(it) }
                PlayerAvatar("Luis Chávez")
            }
        }
    }
}
