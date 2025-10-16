package org.hallen.commandhub.platform

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

/**
 * Gestor del System Tray para Windows
 * Permite minimizar la aplicación a la bandeja del sistema
 */
class SystemTrayManager(
    private val onShow: () -> Unit,
    private val onExit: () -> Unit
) {
    private var tray: SystemTray? = null
    private var trayIcon: TrayIcon? = null
    
    fun setup() {
        if (!SystemTray.isSupported()) {
            println("System tray no es soportado en este sistema")
            return
        }
        
        try {
            tray = SystemTray.getSystemTray()
            
            // Crear el icono del tray
            val image = createTrayImage()
            
            // Crear menú contextual
            val popup = PopupMenu()
            
            val showItem = MenuItem("Mostrar CommandHub")
            showItem.addActionListener {
                SwingUtilities.invokeLater {
                    onShow()
                }
            }
            
            val exitItem = MenuItem("Salir")
            exitItem.addActionListener {
                SwingUtilities.invokeLater {
                    onExit()
                }
            }
            
            popup.add(showItem)
            popup.addSeparator()
            popup.add(exitItem)
            
            // Crear TrayIcon
            trayIcon = TrayIcon(image, "CommandHub", popup).apply {
                isImageAutoSize = true
                
                // Doble click para mostrar
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                            SwingUtilities.invokeLater {
                                onShow()
                            }
                        }
                    }
                })
            }
            
            tray?.add(trayIcon)
            println("System tray configurado correctamente")
            
        } catch (e: Exception) {
            println("Error al configurar system tray: ${e.message}")
            e.printStackTrace()
        }
    }
    
    fun remove() {
        trayIcon?.let { icon ->
            tray?.remove(icon)
        }
        trayIcon = null
        tray = null
    }
    
    fun showNotification(title: String, message: String, type: TrayIcon.MessageType = TrayIcon.MessageType.INFO) {
        trayIcon?.displayMessage(title, message, type)
    }
    
    private fun createTrayImage(): Image {
        // Intentar cargar el icono desde recursos
        val iconFromResources = loadIconFromResources()
        if (iconFromResources != null) {
            return iconFromResources
        }
        
        // Si no existe, crear un icono generado con "CH"
        val size = 16
        val image = java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        
        // Activar antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        // Fondo con gradiente azul
        val gradient = GradientPaint(0f, 0f, Color(42, 104, 255), size.toFloat(), size.toFloat(), Color(26, 80, 204))
        g.paint = gradient
        g.fillRoundRect(0, 0, size, size, 4, 4)
        
        // Dibujar "CH" en blanco
        g.color = Color.WHITE
        g.font = Font("Arial", Font.BOLD, 10)
        val fm = g.fontMetrics
        val text = "C"
        val x = (size - fm.stringWidth(text)) / 2
        val y = ((size - fm.height) / 2) + fm.ascent
        g.drawString(text, x, y)
        
        g.dispose()
        return image
    }
    
    /**
     * Intenta cargar el icono desde los recursos de la aplicación
     */
    private fun loadIconFromResources(): Image? {
        return try {
            // Intentar cargar desde el classpath (recursos empaquetados)
            val resourceStream = this::class.java.getResourceAsStream("/drawable/app_icon.png")
            if (resourceStream != null) {
                println("✅ Icono del system tray cargado desde recursos empaquetados")
                return ImageIO.read(resourceStream)
            }
            
            // Intentar múltiples rutas para desarrollo
            val possiblePaths = listOf(
                "composeApp/src/jvmMain/composeResources/drawable/app_icon.png",
                "src/jvmMain/composeResources/drawable/app_icon.png",
                "../composeApp/src/jvmMain/composeResources/drawable/app_icon.png"
            )
            
            for (path in possiblePaths) {
                val iconFile = File(path)
                if (iconFile.exists()) {
                    println("✅ Icono del system tray cargado desde: ${iconFile.absolutePath}")
                    return ImageIO.read(iconFile)
                }
            }
            
            println("⚠️  No se encontró app_icon.png para system tray, usando icono generado")
            null
        } catch (e: Exception) {
            println("❌ Error al cargar icono personalizado del system tray: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
