package org.hallen.commandhub.terminal

import androidx.compose.ui.graphics.Color
import org.hallen.commandhub.domain.model.AnsiColorScheme

/**
 * Mapea códigos de color ANSI a colores de Compose
 */
object AnsiColorMapper {
    
    // === DEFAULT THEME ===
    private val defaultBasicColors = listOf(
        Color(0xFF000000), // 0: Black
        Color(0xFFCD3131), // 1: Red
        Color(0xFF0DBC79), // 2: Green
        Color(0xFFE5E510), // 3: Yellow
        Color(0xFF2472C8), // 4: Blue
        Color(0xFFBC3FBC), // 5: Magenta
        Color(0xFF11A8CD), // 6: Cyan
        Color(0xFFE5E5E5)  // 7: White
    )
    
    private val defaultBrightColors = listOf(
        Color(0xFF666666), // 8: Bright Black (Gray)
        Color(0xFFF14C4C), // 9: Bright Red
        Color(0xFF23D18B), // 10: Bright Green
        Color(0xFFF5F543), // 11: Bright Yellow
        Color(0xFF3B8EEA), // 12: Bright Blue
        Color(0xFFD670D6), // 13: Bright Magenta
        Color(0xFF29B8DB), // 14: Bright Cyan
        Color(0xFFFFFFFF)  // 15: Bright White
    )
    
    // === DRACULA THEME ===
    private val draculaBasicColors = listOf(
        Color(0xFF282A36), // 0: Black
        Color(0xFFFF5555), // 1: Red
        Color(0xFF50FA7B), // 2: Green
        Color(0xFFF1FA8C), // 3: Yellow
        Color(0xFFBD93F9), // 4: Blue
        Color(0xFFFF79C6), // 5: Magenta
        Color(0xFF8BE9FD), // 6: Cyan
        Color(0xFFF8F8F2)  // 7: White
    )
    
    private val draculaBrightColors = listOf(
        Color(0xFF6272A4), // 8: Bright Black
        Color(0xFFFF6E6E), // 9: Bright Red
        Color(0xFF69FF94), // 10: Bright Green
        Color(0xFFFFFFA5), // 11: Bright Yellow
        Color(0xFFD6ACFF), // 12: Bright Blue
        Color(0xFFFF92DF), // 13: Bright Magenta
        Color(0xFFA4FFFF), // 14: Bright Cyan
        Color(0xFFFFFFFF)  // 15: Bright White
    )
    
    // === NORD THEME ===
    private val nordBasicColors = listOf(
        Color(0xFF2E3440), // 0: Black
        Color(0xFFBF616A), // 1: Red
        Color(0xFFA3BE8C), // 2: Green
        Color(0xFFEBCB8B), // 3: Yellow
        Color(0xFF81A1C1), // 4: Blue
        Color(0xFFB48EAD), // 5: Magenta
        Color(0xFF88C0D0), // 6: Cyan
        Color(0xFFE5E9F0)  // 7: White
    )
    
    private val nordBrightColors = listOf(
        Color(0xFF4C566A), // 8: Bright Black
        Color(0xFFD08770), // 9: Bright Red
        Color(0xFF8FBCBB), // 10: Bright Green
        Color(0xFFF4E1A0), // 11: Bright Yellow
        Color(0xFF5E81AC), // 12: Bright Blue
        Color(0xFFB48EAD), // 13: Bright Magenta
        Color(0xFF88C0D0), // 14: Bright Cyan
        Color(0xFFECEFF4)  // 15: Bright White
    )
    
    // === MONOKAI THEME ===
    private val monokaiBasicColors = listOf(
        Color(0xFF272822), // 0: Black
        Color(0xFFF92672), // 1: Red
        Color(0xFFA6E22E), // 2: Green
        Color(0xFFF4BF75), // 3: Yellow
        Color(0xFF66D9EF), // 4: Blue
        Color(0xFFAE81FF), // 5: Magenta
        Color(0xFFA1EFE4), // 6: Cyan
        Color(0xFFF8F8F2)  // 7: White
    )
    
    private val monokaiBrightColors = listOf(
        Color(0xFF75715E), // 8: Bright Black
        Color(0xFFFF6188), // 9: Bright Red
        Color(0xFFA9DC76), // 10: Bright Green
        Color(0xFFFFD866), // 11: Bright Yellow
        Color(0xFF78DCE8), // 12: Bright Blue
        Color(0xFFAB9DF2), // 13: Bright Magenta
        Color(0xFFA1EFE4), // 14: Bright Cyan
        Color(0xFFFFFFFF)  // 15: Bright White
    )
    
    // Estado actual del esquema
    private var currentScheme = AnsiColorScheme.DEFAULT
    
    /**
     * Cambia el esquema de colores actual
     */
    fun setColorScheme(scheme: AnsiColorScheme) {
        currentScheme = scheme
    }
    
    /**
     * Obtiene los colores básicos según el esquema actual
     */
    private fun getBasicColors(): List<Color> {
        return when (currentScheme) {
            AnsiColorScheme.DEFAULT -> defaultBasicColors
            AnsiColorScheme.DRACULA -> draculaBasicColors
            AnsiColorScheme.NORD -> nordBasicColors
            AnsiColorScheme.MONOKAI -> monokaiBasicColors
            else -> defaultBasicColors // Para otros esquemas por ahora
        }
    }
    
    /**
     * Obtiene los colores brillantes según el esquema actual
     */
    private fun getBrightColors(): List<Color> {
        return when (currentScheme) {
            AnsiColorScheme.DEFAULT -> defaultBrightColors
            AnsiColorScheme.DRACULA -> draculaBrightColors
            AnsiColorScheme.NORD -> nordBrightColors
            AnsiColorScheme.MONOKAI -> monokaiBrightColors
            else -> defaultBrightColors
        }
    }
    
    /**
     * Obtiene un color a partir de un código ANSI (30-37, 40-47, 90-97, 100-107)
     */
    fun getColor(code: Int): Color? {
        val basicColors = getBasicColors()
        val brightColors = getBrightColors()
        
        return when (code) {
            // Foreground colors (30-37)
            in 30..37 -> basicColors[code - 30]
            // Background colors (40-47)
            in 40..47 -> basicColors[code - 40]
            // Bright foreground colors (90-97)
            in 90..97 -> brightColors[code - 90]
            // Bright background colors (100-107)
            in 100..107 -> brightColors[code - 100]
            // Default color
            39, 49 -> null
            else -> null
        }
    }
    
    /**
     * Obtiene un color de la paleta de 256 colores (0-255)
     */
    fun get256Color(index: Int): Color? {
        val basicColors = getBasicColors()
        val brightColors = getBrightColors()
        
        return when (index) {
            // Colores básicos (0-7)
            in 0..7 -> basicColors[index]
            // Colores brillantes (8-15)
            in 8..15 -> brightColors[index - 8]
            // Cubo de 6x6x6 (16-231)
            in 16..231 -> {
                val i = index - 16
                val r = (i / 36) * 51
                val g = ((i % 36) / 6) * 51
                val b = (i % 6) * 51
                Color(r, g, b)
            }
            // Escala de grises (232-255)
            in 232..255 -> {
                val gray = 8 + (index - 232) * 10
                Color(gray, gray, gray)
            }
            else -> null
        }
    }
    
    /**
     * Crea un color RGB verdadero (TrueColor / 24-bit)
     */
    fun getRGBColor(r: Int, g: Int, b: Int): Color {
        return Color(r, g, b)
    }
    
    /**
     * Obtiene el color de fondo recomendado para el esquema actual
     */
    fun getTerminalBackground(): Color {
        return when (currentScheme) {
            AnsiColorScheme.DEFAULT -> Color(0xFF0D0D0D)
            AnsiColorScheme.DRACULA -> Color(0xFF282A36)
            AnsiColorScheme.NORD -> Color(0xFF2E3440)
            AnsiColorScheme.MONOKAI -> Color(0xFF272822)
            else -> Color(0xFF0D0D0D)
        }
    }
    
    /**
     * Obtiene el color de texto por defecto para el esquema actual
     */
    fun getDefaultTextColor(): Color {
        return when (currentScheme) {
            AnsiColorScheme.DEFAULT -> Color(0xFFE0E0E0)
            AnsiColorScheme.DRACULA -> Color(0xFFF8F8F2)
            AnsiColorScheme.NORD -> Color(0xFFE5E9F0)
            AnsiColorScheme.MONOKAI -> Color(0xFFF8F8F2)
            else -> Color(0xFFE0E0E0)
        }
    }
}
