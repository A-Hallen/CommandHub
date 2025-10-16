package org.hallen.commandhub.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/**
 * Factory para crear el driver de SQLDelight en JVM
 */
class DatabaseDriverFactory {
    
    fun createDriver(): SqlDriver {
        try {
            println("Iniciando creación de driver de base de datos")
            
            // Directorio de la aplicación en AppData
            val userHome = System.getProperty("user.home")
            val appDir = File(userHome, ".commandhub")
            
            if (!appDir.exists()) {
                println("Creando directorio: ${appDir.absolutePath}")
                appDir.mkdirs()
            }
            
            val databasePath = File(appDir, "commandhub.db")
            val isNewDatabase = !databasePath.exists()
            
            println("Ruta de BD: ${databasePath.absolutePath}")
            println("BD nueva: $isNewDatabase")
            
            val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
            
            // Crear tablas solo si la base de datos es nueva
            if (isNewDatabase) {
                println("Creando esquema...")
                CommandHubDatabase.Schema.create(driver)
                println("Esquema creado")
            }
            
            println("Driver creado exitosamente")
            return driver
        } catch (e: Exception) {
            println("ERROR al crear driver: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
