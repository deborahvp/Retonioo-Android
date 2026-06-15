package com.example.retonioandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema claro fijo: el neominimalismo de Retoño depende de una paleta concreta,
 * así que NO usamos dynamic color (que tomaría los colores del wallpaper del usuario).
 */
private val RetonioColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = GreenSoft,
    onPrimaryContainer = GreenPrimary,
    secondary = WarmAccent,
    onSecondary = SurfaceWhite,
    tertiary = WarmAccent,
    background = Bone,
    onBackground = InkText,
    surface = SurfaceWhite,
    onSurface = InkText,
    surfaceVariant = GreenSoft,
    onSurfaceVariant = InkSecondary,
    outline = HairlineBorder,
    outlineVariant = HairlineBorder,
)

@Composable
fun RetonioAndroidTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RetonioColorScheme,
        typography = Typography,
        content = content,
    )
}
