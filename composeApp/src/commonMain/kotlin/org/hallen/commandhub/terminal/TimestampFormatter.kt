package org.hallen.commandhub.terminal

import org.hallen.commandhub.domain.model.TimestampFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Utilidades para formatear timestamps en el terminal
 */
object TimestampFormatter {
    
    /**
     * Formatea un timestamp según el formato especificado
     */
    fun format(timestamp: Long, format: TimestampFormat): String {
        return when (format) {
            TimestampFormat.NONE -> ""
            TimestampFormat.TIME_ONLY -> {
                val sdf = SimpleDateFormat("HH:mm:ss")
                sdf.format(Date(timestamp))
            }
            TimestampFormat.TIME_MS -> {
                val sdf = SimpleDateFormat("HH:mm:ss.SSS")
                sdf.format(Date(timestamp))
            }
            TimestampFormat.DATE_TIME -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                sdf.format(Date(timestamp))
            }
            TimestampFormat.RELATIVE -> {
                formatRelativeTime(timestamp)
            }
            TimestampFormat.UNIX -> timestamp.toString()
        }
    }
    
    /**
     * Formatea un tiempo relativo (hace X minutos)
     */
    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 1000 -> "ahora"
            diff < 60000 -> "${diff / 1000}s"
            diff < 3600000 -> "${diff / 60000}m"
            diff < 86400000 -> "${diff / 3600000}h"
            else -> "${diff / 86400000}d"
        }
    }
    
    /**
     * Agrega timestamps a cada línea de texto
     */
    fun addTimestampsToText(
        text: String,
        format: TimestampFormat,
        currentTime: Long = System.currentTimeMillis()
    ): String {
        if (format == TimestampFormat.NONE || text.isEmpty()) {
            return text
        }
        
        val lines = text.split("\n")
        return lines.joinToString("\n") { line ->
            if (line.isNotEmpty()) {
                val timestamp = format(currentTime, format)
                "[$timestamp] $line"
            } else {
                line
            }
        }
    }
}
