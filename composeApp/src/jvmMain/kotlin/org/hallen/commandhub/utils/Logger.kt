package org.hallen.commandhub.utils

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Sistema de logging que escribe a archivo para diagnóstico en producción
 */
object Logger {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private var logFile: File? = null
    private var writer: PrintWriter? = null
    private var initialized = false

    private fun ensureInitialized() {
        if (initialized) return

        try {
            val appDir = File(System.getProperty("user.home"), ".commandhub")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            logFile = File(appDir, "commandhub.log")
            writer = logFile?.let { FileWriter(it, true) }?.let { PrintWriter(it, true) }
            initialized = true

            log("INFO", "Logger iniciado. Archivo: ${logFile?.absolutePath}")
        } catch (e: Exception) {
            System.err.println("Error al inicializar logger: ${e.message}")
            e.printStackTrace()
        }
    }

    fun log(level: String, message: String) {
        ensureInitialized()
        try {
            val timestamp = dateFormat.format(Date())
            val logMessage = "[$timestamp] [$level] $message"

            // Escribir a archivo
            writer?.println(logMessage)
            writer?.flush()

            // También escribir a consola
            println(logMessage)
        } catch (e: Exception) {
            System.err.println("Error al escribir log: ${e.message}")
        }
    }

    fun info(message: String) {
        ensureInitialized()
        log("INFO", message)
    }

    fun error(message: String) {
        ensureInitialized()
        log("ERROR", message)
    }

    fun warn(message: String) {
        ensureInitialized()
        log("WARN", message)
    }

    fun debug(message: String) {
        ensureInitialized()
        log("DEBUG", message)
    }

    fun exception(message: String, e: Exception) {
        ensureInitialized()
        error("$message: ${e.message}")
        e.printStackTrace(writer)
        writer?.flush()
    }

    fun getLogFilePath(): String? {
        ensureInitialized()
        return logFile?.absolutePath
    }
}
