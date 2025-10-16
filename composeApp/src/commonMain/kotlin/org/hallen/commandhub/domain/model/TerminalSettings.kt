package org.hallen.commandhub.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Configuraciones del terminal
 */
data class TerminalSettings(
    // === APARIENCIA ===
    val fontSize: Int = 13,
    val lineHeight: Int = 20,
    val fontFamily: TerminalFontFamily = TerminalFontFamily.MONOSPACE,
    val backgroundColor: TerminalBackground = TerminalBackground.AUTO,
    val textColor: TerminalTextColor = TerminalTextColor.AUTO,
    
    // === COLORES ANSI ===
    val colorScheme: AnsiColorScheme = AnsiColorScheme.DEFAULT,
    val use256Colors: Boolean = true,
    val useTrueColor: Boolean = true,
    
    // === COMPORTAMIENTO ===
    val autoScroll: Boolean = true,
    val smoothScroll: Boolean = true,
    val selectionEnabled: Boolean = true,
    val wordWrap: Boolean = false,
    
    // === DISPLAY ===
    val showTimestamps: Boolean = false,
    val timestampFormat: TimestampFormat = TimestampFormat.TIME_ONLY,
    val showLineNumbers: Boolean = false,
    val highlightErrors: Boolean = true,
    val highlightWarnings: Boolean = true,
    
    // === PERFORMANCE ===
    val maxLines: Int = 10000,
    val bufferSize: Int = 1000,
    val enableVirtualization: Boolean = true,
    
    // === INTERACTIVIDAD ===
    val enableInput: Boolean = false, // Para FASE 3
    val historySize: Int = 100,
    val autoComplete: Boolean = true,
    val cursorBlink: Boolean = true,
    
    // === CLIPBOARD ===
    val copyWithFormatting: Boolean = false,
    val trimCopiedText: Boolean = true,
    val autoSelectOnDoubleClick: Boolean = true,
    
    // === AVANZADO ===
    val parseAnsiCodes: Boolean = true,
    val showControlCharacters: Boolean = false,
    val bellSound: Boolean = false
)

/**
 * Fuentes disponibles para el terminal
 */
enum class TerminalFontFamily {
    MONOSPACE,
    COURIER_NEW,
    CONSOLAS,
    FIRA_CODE,
    JETBRAINS_MONO,
    SOURCE_CODE_PRO
}

/**
 * Configuración de fondo del terminal
 */
enum class TerminalBackground {
    AUTO,           // Se adapta al tema
    DARK,           // Siempre oscuro
    LIGHT,          // Siempre claro
    TRANSPARENT,    // Transparente
    CUSTOM          // Color personalizado
}

/**
 * Configuración de color de texto
 */
enum class TerminalTextColor {
    AUTO,           // Se adapta al tema
    LIGHT,          // Texto claro
    DARK,           // Texto oscuro
    CUSTOM          // Color personalizado
}

/**
 * Esquemas de colores ANSI predefinidos
 */
enum class AnsiColorScheme {
    DEFAULT,        // Colores por defecto
    SOLARIZED_DARK,
    SOLARIZED_LIGHT,
    MONOKAI,
    DRACULA,
    NORD,
    ONE_DARK,
    GRUVBOX_DARK,
    GRUVBOX_LIGHT,
    TOKYO_NIGHT,
    CUSTOM          // Personalizado
}

/**
 * Formato de timestamps
 */
enum class TimestampFormat {
    NONE,
    TIME_ONLY,      // HH:mm:ss
    TIME_MS,        // HH:mm:ss.SSS
    DATE_TIME,      // yyyy-MM-dd HH:mm:ss
    RELATIVE,       // "hace 2 min"
    UNIX            // Unix timestamp
}

/**
 * Valores por defecto
 */
object TerminalDefaults {
    val DEFAULT = TerminalSettings()
    
    val COMPACT = TerminalSettings(
        fontSize = 11,
        lineHeight = 16,
        maxLines = 5000
    )
    
    val LARGE = TerminalSettings(
        fontSize = 15,
        lineHeight = 24
    )
    
    val DEVELOPER = TerminalSettings(
        fontSize = 13,
        showLineNumbers = true,
        highlightErrors = true,
        highlightWarnings = true,
        enableVirtualization = true,
        maxLines = 50000
    )
    
    val MINIMAL = TerminalSettings(
        fontSize = 13,
        showTimestamps = false,
        showLineNumbers = false,
        autoScroll = true,
        wordWrap = false
    )
}
