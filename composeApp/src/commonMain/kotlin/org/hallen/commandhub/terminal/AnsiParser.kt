package org.hallen.commandhub.terminal

/**
 * Parser de códigos ANSI escape sequences
 * 
 * Parsea texto que contiene códigos ANSI y lo convierte en una lista de segmentos
 * con sus respectivos estilos aplicados.
 * 
 * Soporta:
 * - Colores básicos (30-37, 40-47)
 * - Colores brillantes (90-97, 100-107)
 * - Colores de 256 colores (ESC[38;5;Nm, ESC[48;5;Nm)
 * - TrueColor RGB (ESC[38;2;r;g;bm, ESC[48;2;r;g;bm)
 * - Estilos de texto (bold, dim, italic, underline, strikethrough, inverse)
 * - Reset (ESC[0m)
 */
object AnsiParser {
    
    // Regex para encontrar secuencias ANSI
    // Captura ESC[...m o ESC[...H (cursor) o ESC[...J (clear)
    private val ansiRegex = Regex("\u001B\\[(\\d+(?:;\\d+)*)?([mHJKABCDEFGsuhl])")
    
    /**
     * Parsea texto con códigos ANSI y retorna una lista de segmentos con estilos
     */
    fun parse(text: String): List<AnsiSegment> {
        if (text.isEmpty()) return emptyList()
        
        val segments = mutableListOf<AnsiSegment>()
        var currentState = AnsiState()
        var lastIndex = 0
        
        // Encontrar todas las secuencias ANSI
        ansiRegex.findAll(text).forEach { match ->
            // Agregar el texto antes de esta secuencia ANSI (si existe)
            if (match.range.first > lastIndex) {
                val textBefore = text.substring(lastIndex, match.range.first)
                if (textBefore.isNotEmpty()) {
                    segments.add(currentState.toSegment(textBefore))
                }
            }
            
            // Procesar la secuencia ANSI
            val params = match.groupValues[1]
            val command = match.groupValues[2]
            
            // Solo procesamos comandos 'm' (SGR - Select Graphic Rendition)
            if (command == "m") {
                currentState = processSGR(currentState, params)
            }
            // Ignoramos otros comandos (cursor, clear, etc.) por ahora
            
            lastIndex = match.range.last + 1
        }
        
        // Agregar el texto restante después de la última secuencia
        if (lastIndex < text.length) {
            val remainingText = text.substring(lastIndex)
            if (remainingText.isNotEmpty()) {
                segments.add(currentState.toSegment(remainingText))
            }
        }
        
        return segments
    }
    
    /**
     * Procesa códigos SGR (Select Graphic Rendition) - los códigos 'm'
     */
    private fun processSGR(state: AnsiState, params: String): AnsiState {
        if (params.isEmpty()) {
            // ESC[m es equivalente a ESC[0m (reset)
            return state.reset()
        }
        
        val codes = params.split(";").mapNotNull { it.toIntOrNull() }
        var newState = state
        var i = 0
        
        while (i < codes.size) {
            val code = codes[i]
            
            when (code) {
                // Reset
                0 -> newState = newState.reset()
                
                // Estilos de texto
                1 -> newState = newState.copy(bold = true)
                2 -> newState = newState.copy(dim = true)
                3 -> newState = newState.copy(italic = true)
                4 -> newState = newState.copy(underline = true)
                7 -> newState = newState.copy(inverse = true)
                9 -> newState = newState.copy(strikethrough = true)
                
                // Reset de estilos específicos
                22 -> newState = newState.copy(bold = false, dim = false)
                23 -> newState = newState.copy(italic = false)
                24 -> newState = newState.copy(underline = false)
                27 -> newState = newState.copy(inverse = false)
                29 -> newState = newState.copy(strikethrough = false)
                
                // Colores foreground básicos (30-37)
                in 30..37 -> {
                    newState = newState.copy(foregroundColor = AnsiColorMapper.getColor(code))
                }
                
                // Foreground: 256 colors o TrueColor
                38 -> {
                    if (i + 1 < codes.size) {
                        when (codes[i + 1]) {
                            // 256 colors: ESC[38;5;Nm
                            5 -> {
                                if (i + 2 < codes.size) {
                                    val colorIndex = codes[i + 2]
                                    newState = newState.copy(
                                        foregroundColor = AnsiColorMapper.get256Color(colorIndex)
                                    )
                                    i += 2
                                }
                            }
                            // TrueColor: ESC[38;2;r;g;bm
                            2 -> {
                                if (i + 4 < codes.size) {
                                    val r = codes[i + 2]
                                    val g = codes[i + 3]
                                    val b = codes[i + 4]
                                    newState = newState.copy(
                                        foregroundColor = AnsiColorMapper.getRGBColor(r, g, b)
                                    )
                                    i += 4
                                }
                            }
                        }
                    }
                }
                
                // Default foreground color
                39 -> newState = newState.copy(foregroundColor = null)
                
                // Colores background básicos (40-47)
                in 40..47 -> {
                    newState = newState.copy(backgroundColor = AnsiColorMapper.getColor(code))
                }
                
                // Background: 256 colors o TrueColor
                48 -> {
                    if (i + 1 < codes.size) {
                        when (codes[i + 1]) {
                            // 256 colors: ESC[48;5;Nm
                            5 -> {
                                if (i + 2 < codes.size) {
                                    val colorIndex = codes[i + 2]
                                    newState = newState.copy(
                                        backgroundColor = AnsiColorMapper.get256Color(colorIndex)
                                    )
                                    i += 2
                                }
                            }
                            // TrueColor: ESC[48;2;r;g;bm
                            2 -> {
                                if (i + 4 < codes.size) {
                                    val r = codes[i + 2]
                                    val g = codes[i + 3]
                                    val b = codes[i + 4]
                                    newState = newState.copy(
                                        backgroundColor = AnsiColorMapper.getRGBColor(r, g, b)
                                    )
                                    i += 4
                                }
                            }
                        }
                    }
                }
                
                // Default background color
                49 -> newState = newState.copy(backgroundColor = null)
                
                // Colores foreground brillantes (90-97)
                in 90..97 -> {
                    newState = newState.copy(foregroundColor = AnsiColorMapper.getColor(code))
                }
                
                // Colores background brillantes (100-107)
                in 100..107 -> {
                    newState = newState.copy(backgroundColor = AnsiColorMapper.getColor(code))
                }
            }
            
            i++
        }
        
        return newState
    }
    
    /**
     * Elimina todos los códigos ANSI del texto y retorna texto plano
     * Útil para copiar texto sin formato
     */
    fun stripAnsiCodes(text: String): String {
        return ansiRegex.replace(text, "")
    }
}
