package org.hallen.commandhub.terminal

import androidx.compose.ui.graphics.Color

/**
 * Representa un segmento de texto con estilo ANSI aplicado
 */
data class AnsiSegment(
    val text: String,
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val bold: Boolean = false,
    val dim: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val inverse: Boolean = false
)

/**
 * Estado acumulativo de estilos ANSI mientras se parsea
 */
data class AnsiState(
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val bold: Boolean = false,
    val dim: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val inverse: Boolean = false
) {
    /**
     * Resetea todos los estilos al estado por defecto
     */
    fun reset() = AnsiState()
    
    /**
     * Convierte el estado actual en un segmento de texto
     */
    fun toSegment(text: String) = AnsiSegment(
        text = text,
        foregroundColor = if (inverse) backgroundColor else foregroundColor,
        backgroundColor = if (inverse) foregroundColor else backgroundColor,
        bold = bold,
        dim = dim,
        italic = italic,
        underline = underline,
        strikethrough = strikethrough,
        inverse = inverse
    )
}
