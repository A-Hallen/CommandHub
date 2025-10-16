package org.hallen.commandhub.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * Provider del tema de la aplicación
 * Envuelve toda la app y provee los colores según el tema seleccionado
 */
@Composable
fun AppThemeProvider(
    themeManager: ThemeManager = remember { ThemeManager() },
    content: @Composable () -> Unit
) {
    val themeMode by themeManager.themeMode
    val appColors = themeManager.getCurrentColors()
    
    // Crear MaterialTheme colors basado en nuestros colores custom
    val materialColors = if (themeMode == ThemeMode.DARK) {
        darkColorScheme(
            primary = appColors.primary,
            onPrimary = Color.White,
            primaryContainer = appColors.primaryLight,
            onPrimaryContainer = appColors.textPrimary,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.cardBackground,
            onSurfaceVariant = appColors.textSecondary,
            error = appColors.danger,
            onError = Color.White,
            outline = appColors.border
        )
    } else {
        lightColorScheme(
            primary = appColors.primary,
            onPrimary = Color.White,
            primaryContainer = appColors.primaryLight,
            onPrimaryContainer = appColors.textPrimary,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.cardBackground,
            onSurfaceVariant = appColors.textSecondary,
            error = appColors.danger,
            onError = Color.White,
            outline = appColors.border
        )
    }
    
    CompositionLocalProvider(
        LocalThemeManager provides themeManager,
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}
