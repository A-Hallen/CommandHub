package org.hallen.commandhub.terminal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.hallen.commandhub.domain.model.TerminalSettings
import org.hallen.commandhub.domain.model.TerminalDefaults
import org.hallen.commandhub.domain.model.TerminalFontFamily
import org.hallen.commandhub.domain.model.AnsiColorScheme
import org.hallen.commandhub.domain.model.TerminalBackground
import org.hallen.commandhub.domain.model.TerminalTextColor
import org.hallen.commandhub.domain.model.TimestampFormat

/**
 * Manager para las configuraciones del terminal
 * Maneja el estado y persistencia de las configuraciones
 */
class TerminalSettingsManager {
    val settings = mutableStateOf(TerminalDefaults.DEFAULT)
    
    // Funciones de actualización individual
    fun updateFontSize(size: Int) {
        settings.value = settings.value.copy(fontSize = size)
        persistSettings()
    }
    
    fun updateLineHeight(height: Int) {
        settings.value = settings.value.copy(lineHeight = height)
        persistSettings()
    }
    
    fun updateFontFamily(family: TerminalFontFamily) {
        settings.value = settings.value.copy(fontFamily = family)
        persistSettings()
    }
    
    fun updateAutoScroll(enabled: Boolean) {
        settings.value = settings.value.copy(autoScroll = enabled)
        persistSettings()
    }
    
    fun updateSmoothScroll(enabled: Boolean) {
        settings.value = settings.value.copy(smoothScroll = enabled)
        persistSettings()
    }
    
    fun updateShowTimestamps(enabled: Boolean) {
        settings.value = settings.value.copy(showTimestamps = enabled)
        persistSettings()
    }
    
    fun updateTimestampFormat(format: TimestampFormat) {
        settings.value = settings.value.copy(timestampFormat = format)
        persistSettings()
    }
    
    fun updateShowLineNumbers(enabled: Boolean) {
        settings.value = settings.value.copy(showLineNumbers = enabled)
        persistSettings()
    }
    
    fun updateWordWrap(enabled: Boolean) {
        settings.value = settings.value.copy(wordWrap = enabled)
        persistSettings()
    }
    
    fun updateColorScheme(scheme: AnsiColorScheme) {
        settings.value = settings.value.copy(colorScheme = scheme)
        AnsiColorMapper.setColorScheme(scheme)
        persistSettings()
    }
    
    fun updateBackgroundColor(background: TerminalBackground) {
        settings.value = settings.value.copy(backgroundColor = background)
        persistSettings()
    }
    
    fun updateTextColor(textColor: TerminalTextColor) {
        settings.value = settings.value.copy(textColor = textColor)
        persistSettings()
    }
    
    fun updateHighlightErrors(enabled: Boolean) {
        settings.value = settings.value.copy(highlightErrors = enabled)
        persistSettings()
    }
    
    fun updateHighlightWarnings(enabled: Boolean) {
        settings.value = settings.value.copy(highlightWarnings = enabled)
        persistSettings()
    }
    
    fun updateMaxLines(lines: Int) {
        settings.value = settings.value.copy(maxLines = lines)
        persistSettings()
    }
    
    fun updateEnableVirtualization(enabled: Boolean) {
        settings.value = settings.value.copy(enableVirtualization = enabled)
        persistSettings()
    }
    
    fun updateParseAnsiCodes(enabled: Boolean) {
        settings.value = settings.value.copy(parseAnsiCodes = enabled)
        persistSettings()
    }
    
    fun updateCopyWithFormatting(enabled: Boolean) {
        settings.value = settings.value.copy(copyWithFormatting = enabled)
        persistSettings()
    }
    
    // Presets rápidos
    fun applyPreset(preset: TerminalSettings) {
        settings.value = preset
        persistSettings()
    }
    
    fun resetToDefaults() {
        settings.value = TerminalDefaults.DEFAULT
        persistSettings()
    }
    
    // Persistencia (TODO: Implementar con DataStore o SharedPreferences)
    private fun persistSettings() {
        // Por ahora solo en memoria
        // En el futuro: guardar en archivo/base de datos
        println("Terminal settings updated: ${settings.value}")
    }
    
    fun loadSettings() {
        // TODO: Cargar desde persistencia
        // Por ahora usa defaults
    }
    
    companion object {
        private var instance: TerminalSettingsManager? = null
        
        fun getInstance(): TerminalSettingsManager {
            if (instance == null) {
                instance = TerminalSettingsManager()
                instance?.loadSettings()
            }
            return instance!!
        }
    }
}

/**
 * Composable para obtener el manager de configuraciones del terminal
 */
@Composable
fun rememberTerminalSettingsManager(): TerminalSettingsManager {
    return remember { TerminalSettingsManager.getInstance() }
}

/**
 * Composable para obtener las configuraciones actuales del terminal
 */
@Composable
fun useTerminalSettings(): TerminalSettings {
    val manager = rememberTerminalSettingsManager()
    return manager.settings.value
}
