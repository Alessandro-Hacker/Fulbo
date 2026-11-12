package pe.fulbo.app.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val FulboColorScheme = darkColorScheme(
    primary = FulboColors.Green,
    onPrimary = FulboColors.OnGreen,
    primaryContainer = FulboColors.GreenContainer,
    onPrimaryContainer = FulboColors.Green,
    secondary = FulboColors.Blue,
    tertiary = FulboColors.Orange,
    background = FulboColors.Background,
    onBackground = FulboColors.TextPrimary,
    surface = FulboColors.Background,
    onSurface = FulboColors.TextPrimary,
    surfaceVariant = FulboColors.Surface,
    onSurfaceVariant = FulboColors.TextSecondary,
    surfaceContainer = FulboColors.Surface,
    surfaceContainerHigh = FulboColors.SurfaceHigh,
    outline = FulboColors.Outline,
    outlineVariant = FulboColors.Outline,
    error = FulboColors.Red,
)

private val FulboShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)

/** Espaciados estándar. Úsalos en vez de números sueltos: MaterialTheme.spacing.md */
data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val screen: Dp = 20.dp, // margen lateral de pantallas
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable get() = LocalSpacing.current

/** La app es solo tema oscuro, como en los mockups. */
@Composable
fun FulboTheme(content: @Composable () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = FulboColorScheme,
            typography = FulboTypography,
            shapes = FulboShapes,
            content = content,
        )
    }
}
