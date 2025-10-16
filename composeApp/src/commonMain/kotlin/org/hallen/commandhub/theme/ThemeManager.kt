package org.hallen.commandhub.theme

import androidx.compose.runtime.*
import java.io.File
import java.util.Properties

/**
 * Manager para gestionar el tema de la aplicación con persistencia
 */
class ThemeManager {
    private val _themeMode = mutableStateOf(loadSavedTheme())
    val themeMode: State<ThemeMode> = _themeMode
    
    private val prefsFile = File(System.getProperty("user.home"), ".commandhub/preferences.properties")
    
    init {
        // Asegurar que el directorio existe
        prefsFile.parentFile?.mkdirs()
    }
    
    /**
     * Carga el tema guardado de las preferencias
     */
    private fun loadSavedTheme(): ThemeMode {
        return try {
            if (prefsFile.exists()) {
                val props = Properties()
                prefsFile.inputStream().use { props.load(it) }
                val themeName = props.getProperty("theme", "DARK")
                ThemeMode.valueOf(themeName)
            } else {
                ThemeMode.DARK // Por defecto oscuro
            }
        } catch (e: Exception) {
            println("Error loading theme preferences: ${e.message}")
            ThemeMode.DARK
        }
    }
    
    /**
     * Guarda el tema en las preferencias
     */
    private fun saveTheme(mode: ThemeMode) {
        try {
            val props = Properties()
            if (prefsFile.exists()) {
                prefsFile.inputStream().use { props.load(it) }
            }
            props.setProperty("theme", mode.name)
            prefsFile.outputStream().use { props.store(it, "CommandHub Preferences") }
        } catch (e: Exception) {
            println("Error saving theme preferences: ${e.message}")
        }
    }
    
    fun setTheme(mode: ThemeMode) {
        _themeMode.value = mode
        saveTheme(mode)
    }
    
    fun toggleTheme() {
        val newTheme = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
        setTheme(newTheme)
    }
    
    fun getCurrentColors(): AppColors {
        return when (_themeMode.value) {
            ThemeMode.LIGHT -> LightThemeColors
            ThemeMode.DARK -> DarkThemeColors
            ThemeMode.SYSTEM -> LightThemeColors // TODO: Detectar tema del sistema
        }
    }
}

/**
 * CompositionLocal para acceder al ThemeManager en toda la app
 */
val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("No ThemeManager provided")
}

/**
 * CompositionLocal para acceder a los colores del tema actual
 */
val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided")
}

/**
 * Hook para acceder al ThemeManager
 */
@Composable
fun useThemeManager(): ThemeManager {
    return LocalThemeManager.current
}

/**
 * Hook para acceder a los colores del tema actual
 */
@Composable
fun useAppColors(): AppColors {
    return LocalAppColors.current
}
