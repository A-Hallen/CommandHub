package org.hallen.commandhub

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.hallen.commandhub.di.appModule
import org.hallen.commandhub.di.platformModule
import org.hallen.commandhub.platform.SystemTrayManager
import org.hallen.commandhub.presentation.MainScreen
import org.hallen.commandhub.presentation.MainViewModel
import org.hallen.commandhub.theme.AppThemeProvider
import org.hallen.commandhub.theme.ThemeMode
import org.hallen.commandhub.theme.ThemeManager
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import java.awt.Color as AwtColor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    // Inicializar Koin ANTES del composable
    startKoin {
        modules(platformModule, appModule)
    }
    
    application {
        var isVisible by remember { mutableStateOf(true) }
        var shouldExit by remember { mutableStateOf(false) }
        
        // Crear ThemeManager
        val themeManager = remember { ThemeManager() }
        
        // Configurar System Tray
        val trayManager = remember {
            SystemTrayManager(
                onShow = {
                    isVisible = true
                },
                onExit = {
                    shouldExit = true
                    exitApplication()
                }
            )
        }
        
        LaunchedEffect(Unit) {
            trayManager.setup()
        }
        
        DisposableEffect(Unit) {
            onDispose {
                trayManager.remove()
            }
        }
        
        val windowState = rememberWindowState(
            size = DpSize(1200.dp, 800.dp)
        )
        
        // Observar cambios de tema para actualizar la barra de título
        val currentTheme by themeManager.themeMode
        val isDarkMode = currentTheme == ThemeMode.DARK
        
        // Cargar el icono personalizado usando AWT (no composable)
        val appIcon = remember {
            loadAppIcon()
        }
        
        Window(
            onCloseRequest = {
                // Minimizar a tray en lugar de cerrar (sin notificación)
                isVisible = false
            },
            title = "CommandHub",
            state = windowState,
            visible = isVisible,
            alwaysOnTop = false,
            undecorated = false,
            icon = appIcon
        ) {
            // Cambiar color de la barra de título según el tema
            LaunchedEffect(isDarkMode) {
                window.rootPane.putClientProperty("apple.awt.windowTitleVisible", true)
                if (isDarkMode) {
                    // Modo oscuro - fondo negro
                    window.background = AwtColor(10, 10, 10)
                } else {
                    // Modo claro - fondo blanco
                    window.background = AwtColor(244, 247, 250)
                }
            }
            
            AppThemeProvider(themeManager = themeManager) {
                KoinContext {
                    val viewModel = koinViewModel<MainViewModel>()
                    MainScreen(viewModel = viewModel)
                }
            }
        }
        
        // Salir cuando shouldExit sea true
        if (shouldExit) {
            exitApplication()
        }
    }
}

/**
 * Carga el icono de la aplicación desde recursos
 * Intenta cargar app_icon.png, si no existe retorna null
 */
private fun loadAppIcon(): BitmapPainter? {
    return try {
        // Intentar cargar desde el classpath (recursos empaquetados)
        val resourceStream = object {}.javaClass.getResourceAsStream("/drawable/app_icon.png")
        
        val image: BufferedImage? = if (resourceStream != null) {
            println("✅ Icono de ventana cargado desde recursos empaquetados")
            ImageIO.read(resourceStream)
        } else {
            // Intentar múltiples rutas para desarrollo
            val possiblePaths = listOf(
                "composeApp/src/jvmMain/composeResources/drawable/app_icon.png",
                "src/jvmMain/composeResources/drawable/app_icon.png",
                "../composeApp/src/jvmMain/composeResources/drawable/app_icon.png"
            )
            
            var loadedImage: BufferedImage? = null
            for (path in possiblePaths) {
                val iconFile = File(path)
                if (iconFile.exists()) {
                    println("✅ Icono de ventana cargado desde: ${iconFile.absolutePath}")
                    loadedImage = ImageIO.read(iconFile)
                    break
                }
            }
            
            if (loadedImage == null) {
                println("⚠️  No se encontró app_icon.png en rutas de desarrollo, usando icono por defecto")
                println("    Directorio actual: ${File(".").absolutePath}")
            }
            
            loadedImage
        }
        
        image?.let {
            BitmapPainter(it.toComposeImageBitmap())
        }
    } catch (e: Exception) {
        println("❌ Error al cargar icono personalizado: ${e.message}")
        e.printStackTrace()
        null
    }
}