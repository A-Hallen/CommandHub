package org.hallen.commandhub.terminal

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.domain.model.TerminalSettings

/**
 * Composable que renderiza texto con códigos ANSI parseados
 * 
 * Convierte los AnsiSegments a AnnotatedString con estilos aplicados
 * Usa las configuraciones del terminal si no se especifica lo contrario
 */
@Composable
fun AnsiText(
    text: String,
    modifier: Modifier = Modifier,
    settings: TerminalSettings? = null,
    fontSize: TextUnit? = null,
    lineHeight: TextUnit? = null,
    defaultColor: Color? = null,
    selectable: Boolean? = null
) {
    val terminalSettings = settings ?: useTerminalSettings()
    
    // Usar configuraciones del terminal o valores por defecto
    val finalFontSize = fontSize ?: terminalSettings.fontSize.sp
    val finalLineHeight = lineHeight ?: terminalSettings.lineHeight.sp
    val finalSelectable = selectable ?: terminalSettings.selectionEnabled
    
    // Aplicar esquema de colores (esto forzará recomposición cuando cambie)
    AnsiColorMapper.setColorScheme(terminalSettings.colorScheme)
    
    // El color por defecto SIEMPRE viene del esquema ANSI, no del tema de la app
    val terminalDefaultColor = defaultColor ?: AnsiColorMapper.getDefaultTextColor()
    
    // Agregar timestamps si está habilitado
    val processedText = if (terminalSettings.showTimestamps) {
        TimestampFormatter.addTimestampsToText(text, terminalSettings.timestampFormat)
    } else {
        text
    }
    
    // Parsear solo si está habilitado
    val segments = if (terminalSettings.parseAnsiCodes) {
        AnsiParser.parse(processedText)
    } else {
        listOf(AnsiSegment(text = processedText))
    }
    
    val annotatedString = segmentsToAnnotatedString(
        segments = segments,
        defaultColor = terminalDefaultColor
    )
    
    val textContent = @Composable {
        Text(
            text = annotatedString,
            fontFamily = FontFamily.Monospace,
            fontSize = finalFontSize,
            lineHeight = finalLineHeight,
            color = terminalDefaultColor, // Establecer color por defecto explícitamente
            modifier = modifier,
            softWrap = terminalSettings.wordWrap
        )
    }
    
    if (finalSelectable) {
        SelectionContainer {
            textContent()
        }
    } else {
        textContent()
    }
}

/**
 * Convierte una lista de AnsiSegments a AnnotatedString con estilos aplicados
 */
fun segmentsToAnnotatedString(
    segments: List<AnsiSegment>,
    defaultColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        segments.forEach { segment ->
            val spanStyle = createSpanStyle(segment, defaultColor)
            pushStyle(spanStyle)
            append(segment.text)
            pop()
        }
    }
}

/**
 * Crea un SpanStyle a partir de un AnsiSegment
 */
private fun createSpanStyle(segment: AnsiSegment, defaultColor: Color): SpanStyle {
    // SIEMPRE crear un SpanStyle con el color explícito
    // Esto previene que el texto herede el color del tema de la aplicación
    return SpanStyle(
        color = segment.foregroundColor ?: defaultColor,
        background = segment.backgroundColor ?: Color.Transparent,
        fontWeight = when {
            segment.bold -> FontWeight.Bold
            segment.dim -> FontWeight.Light
            else -> FontWeight.Normal
        },
        fontStyle = if (segment.italic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = when {
            segment.underline && segment.strikethrough -> 
                TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
            segment.underline -> TextDecoration.Underline
            segment.strikethrough -> TextDecoration.LineThrough
            else -> null
        }
    )
}

/**
 * Versión simplificada que solo renderiza texto sin selección
 */
@Composable
fun AnsiTextSimple(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 13.sp,
    lineHeight: TextUnit = 20.sp,
    defaultColor: Color? = null
) {
    AnsiText(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = lineHeight,
        defaultColor = defaultColor,
        selectable = false
    )
}
