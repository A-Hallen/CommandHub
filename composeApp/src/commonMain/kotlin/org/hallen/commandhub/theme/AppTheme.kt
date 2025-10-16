package org.hallen.commandhub.theme

import androidx.compose.ui.graphics.Color

/**
 * Modo de tema de la aplicación
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM // Seguir el tema del sistema (para futuro)
}

/**
 * Colores del tema
 */
data class AppColors(
    // Colores primarios
    val primary: Color,
    val primaryHover: Color,
    val primaryLight: Color,
    
    // Fondos
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    
    // Textos
    val textPrimary: Color,
    val textSecondary: Color,
    
    // Bordes y divisores
    val border: Color,
    
    // Estados
    val success: Color,
    val danger: Color,
    val warning: Color,
    
    // Sombras (como string para usar en Modifier)
    val shadow: String,
    val shadowHover: String
)

/**
 * Tema Claro - Paleta basada en verde puro (#008600)
 */
val LightThemeColors = AppColors(
    primary = Color(0xFF007600),
    primaryHover = Color(0xFF006B00),
    primaryLight = Color(0xFFE5F7E5),
    background = Color(0xFFFAFDFC),
    surface = Color(0xFFFFFFFF),
    cardBackground = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1A202C),
    textSecondary = Color(0xFF718096),
    border = Color(0xFFE2E8F0),
    success = Color(0xFF008600),
    danger = Color(0xFFEF4444),
    warning = Color(0xFFF59E0B),
    shadow = "0 4px 12px rgba(0, 0, 0, 0.08)",
    shadowHover = "0 8px 20px rgba(0, 0, 0, 0.12)"
)

/**
 * Tema Oscuro - Paleta basada en verde puro (#00A300)
 */
val DarkThemeColors = AppColors(
    primary = Color(0xFF00A300),
    primaryHover = Color(0xFF008600),
    primaryLight = Color(0xFF1A2F1A),
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF151515),
    cardBackground = Color(0xFF1A1A1A),
    textPrimary = Color(0xFFE4E4E7),
    textSecondary = Color(0xFFA1A1AA),
    border = Color(0xFF2A2A2A),
    success = Color(0xFF00A300),
    danger = Color(0xFFEF4444),
    warning = Color(0xFFF59E0B),
    shadow = "0 4px 12px rgba(0, 0, 0, 0.3)",
    shadowHover = "0 8px 20px rgba(0, 0, 0, 0.4)"
)
