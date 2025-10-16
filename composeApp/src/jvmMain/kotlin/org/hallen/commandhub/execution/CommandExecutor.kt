package org.hallen.commandhub.execution

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.ExecutionStatus
import org.hallen.commandhub.domain.model.ShellType
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Ejecutor de comandos para Windows
 */
class CommandExecutor : CommandExecutorInterface {
    
    // Mantener referencias a procesos activos
    private val activeProcesses = mutableMapOf<String, Process>()
    
    // Mantener referencias a los OutputStreams de los procesos para enviar entrada
    private val processWriters = mutableMapOf<String, java.io.OutputStreamWriter>()
    
    init {
        // Agregar shutdown hook para terminar procesos al cerrar la aplicación
        Runtime.getRuntime().addShutdownHook(Thread {
            println("CommandExecutor - Shutdown hook called, terminating ${activeProcesses.size} processes")
            activeProcesses.values.forEach { process ->
                try {
                    if (process.isAlive) {
                        println("CommandExecutor - Destroying process: ${process.pid()}")
                        process.descendants().forEach { it.destroy() }
                        process.destroy()
                        // Esperar un poco y forzar si es necesario
                        if (!process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                            process.destroyForcibly()
                        }
                    }
                } catch (e: Exception) {
                    println("CommandExecutor - Error destroying process: ${e.message}")
                }
            }
            activeProcesses.clear()
        })
    }

    /**
     * Ejecuta un comando y emite el output en tiempo real
     */
    override fun stopExecution(executionId: String) {
        activeProcesses[executionId]?.let { process ->
            try {
                // Cerrar el writer si existe
                processWriters[executionId]?.let { writer ->
                    try {
                        writer.close()
                    } catch (e: Exception) {
                        println("Error closing writer for $executionId: ${e.message}")
                    }
                    processWriters.remove(executionId)
                }
                
                if (process.isAlive) {
                    // Intentar terminar descendientes primero
                    process.descendants().forEach { it.destroy() }
                    // Terminar el proceso principal
                    process.destroy()
                    // Si no termina en 2 segundos, forzar
                    if (!process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                        process.destroyForcibly()
                    }
                }
                activeProcesses.remove(executionId)
            } catch (e: Exception) {
                println("Error stopping execution $executionId: ${e.message}")
            }
        }
    }
    
    override fun executeCommand(command: Command, executionId: String): Flow<ExecutionOutput> = flow {
        val startTime = System.currentTimeMillis()
        
        println("[CommandExecutor] Starting execution with ID: $executionId")
        emit(ExecutionOutput.Started(startTime))

        try {
            // Crear shell interactivo en lugar de ejecutar comando directamente
            val processBuilder = createInteractiveShell(command)
            val process = withContext(Dispatchers.IO) {
                processBuilder.start()
            }
            
            println("[CommandExecutor] Process started. PID: ${process.pid()}")
            
            // Registrar el proceso activo
            activeProcesses[executionId] = process
            
            // Crear writer para enviar comandos
            val writer = java.io.OutputStreamWriter(process.outputStream, Charsets.UTF_8)
            processWriters[executionId] = writer
            
            println("[CommandExecutor] Writer registered for $executionId")
            
            // Dar un pequeño tiempo para que el shell inicie
            kotlinx.coroutines.delay(200)
            
            // Si hay un comando, enviarlo inmediatamente
            if (command.command.isNotBlank()) {
                writer.write(command.command)
                writer.write("\n")
                writer.flush()
            }

            // Usar canales para leer stdout y stderr en paralelo
            val stdoutChannel = Channel<String>(Channel.UNLIMITED)
            val stderrChannel = Channel<String>(Channel.UNLIMITED)

            // Lanzar coroutines para leer stdout y stderr en paralelo
            val stdoutJob = kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reader = java.io.InputStreamReader(process.inputStream, Charsets.UTF_8)
                    val lineBuffer = StringBuilder()
                    var lastFlushTime = System.currentTimeMillis()
                    
                    while (process.isAlive) {
                        // Leer con timeout no bloqueante
                        if (reader.ready()) {
                            val char = reader.read()
                            if (char == -1) break
                            
                            val c = char.toChar()
                            if (c == '\n') {
                                if (lineBuffer.isNotEmpty()) {
                                    val line = lineBuffer.toString()
                                    println("[CommandExecutor] STDOUT: $line")
                                    stdoutChannel.send(line)
                                    lineBuffer.clear()
                                    lastFlushTime = System.currentTimeMillis()
                                }
                            } else if (c != '\r') {
                                lineBuffer.append(c)
                            }
                        } else {
                            // No hay datos listos - flush si ha pasado tiempo
                            val now = System.currentTimeMillis()
                            if (lineBuffer.isNotEmpty() && (now - lastFlushTime) > 50) {
                                val line = lineBuffer.toString()
                                println("[CommandExecutor] STDOUT (partial): $line")
                                stdoutChannel.send(line)
                                lineBuffer.clear()
                                lastFlushTime = now
                            }
                            kotlinx.coroutines.delay(10)
                        }
                    }
                    
                    // Leer lo que queda
                    while (reader.ready()) {
                        val char = reader.read()
                        if (char == -1) break
                        val c = char.toChar()
                        if (c == '\n') {
                            if (lineBuffer.isNotEmpty()) {
                                stdoutChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                            }
                        } else if (c != '\r') {
                            lineBuffer.append(c)
                        }
                    }
                    
                    if (lineBuffer.isNotEmpty()) {
                        stdoutChannel.send(lineBuffer.toString())
                    }
                } catch (e: Exception) {
                    println("Error reading stdout: ${e.message}")
                } finally {
                    stdoutChannel.close()
                }
            }

            val stderrJob = kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reader = java.io.InputStreamReader(process.errorStream, Charsets.UTF_8)
                    val lineBuffer = StringBuilder()
                    var lastFlushTime = System.currentTimeMillis()
                    
                    while (process.isAlive) {
                        if (reader.ready()) {
                            val char = reader.read()
                            if (char == -1) break
                            
                            val c = char.toChar()
                            if (c == '\n') {
                                if (lineBuffer.isNotEmpty()) {
                                    stderrChannel.send(lineBuffer.toString())
                                    lineBuffer.clear()
                                    lastFlushTime = System.currentTimeMillis()
                                }
                            } else if (c != '\r') {
                                lineBuffer.append(c)
                            }
                        } else {
                            val now = System.currentTimeMillis()
                            if (lineBuffer.isNotEmpty() && (now - lastFlushTime) > 50) {
                                stderrChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                                lastFlushTime = now
                            }
                            kotlinx.coroutines.delay(10)
                        }
                    }
                    
                    while (reader.ready()) {
                        val char = reader.read()
                        if (char == -1) break
                        val c = char.toChar()
                        if (c == '\n') {
                            if (lineBuffer.isNotEmpty()) {
                                stderrChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                            }
                        } else if (c != '\r') {
                            lineBuffer.append(c)
                        }
                    }
                    
                    if (lineBuffer.isNotEmpty()) {
                        stderrChannel.send(lineBuffer.toString())
                    }
                } catch (e: Exception) {
                    println("Error reading stderr: ${e.message}")
                } finally {
                    stderrChannel.close()
                }
            }

            // Leer de ambos canales y emitir
            var stdoutClosed = false
            var stderrClosed = false

            while (!stdoutClosed || !stderrClosed) {
                select<Unit> {
                    if (!stdoutClosed) {
                        stdoutChannel.onReceiveCatching { result ->
                            result.getOrNull()?.let { line ->
                                emit(ExecutionOutput.StdOut(line))
                            } ?: run {
                                stdoutClosed = true
                            }
                        }
                    }
                    if (!stderrClosed) {
                        stderrChannel.onReceiveCatching { result ->
                            result.getOrNull()?.let { line ->
                                emit(ExecutionOutput.StdErr(line))
                            } ?: run {
                                stderrClosed = true
                            }
                        }
                    }
                }
            }

            // Esperar a que el proceso termine
            val exitCode = withContext(Dispatchers.IO) {
                stdoutJob.join()
                stderrJob.join()
                process.waitFor()
            }
            
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            // Remover el proceso de la lista de activos
            activeProcesses.remove(executionId)

            emit(
                ExecutionOutput.Finished(
                    exitCode = exitCode,
                    duration = duration,
                    endTime = endTime
                )
            )
        } catch (e: Exception) {
            // Remover el proceso en caso de error también
            processWriters.remove(executionId)
            activeProcesses.remove(executionId)
            emit(ExecutionOutput.Error(e.message ?: "Error desconocido"))
        }
    }

    private fun createInteractiveShell(command: Command): ProcessBuilder {
        // Crear un shell interactivo basado en el tipo
        val processBuilder = when (command.shell) {
            ShellType.POWERSHELL -> {
                ProcessBuilder(
                    "powershell.exe",
                    "-NoLogo"
                )
            }
            ShellType.CMD -> {
                ProcessBuilder("cmd.exe", "/K")
            }
            ShellType.GIT_BASH -> {
                ProcessBuilder("bash", "-i")
            }
            ShellType.WSL -> {
                ProcessBuilder("wsl", "bash", "-i")
            }
        }

        // Configurar directorio de trabajo
        if (command.workingDirectory.isNotBlank()) {
            val workingDir = File(command.workingDirectory)
            if (workingDir.exists() && workingDir.isDirectory) {
                processBuilder.directory(workingDir)
            }
        }

        // Configurar variables de entorno para mejorar compatibilidad con REPLs
        val env = processBuilder.environment()
        env["PYTHONUNBUFFERED"] = "1"  // Python sin buffer
        env["NODE_NO_READLINE"] = "1"   // Node.js sin readline
        
        processBuilder.redirectErrorStream(false)
        return processBuilder
    }
    
    /**
     * Inicia una sesión de terminal interactiva
     */
    override fun startInteractiveTerminal(
        workingDirectory: String,
        shell: ShellType
    ): Flow<ExecutionOutput> = flow {
        val startTime = System.currentTimeMillis()
        val processId = "interactive_$startTime"
        
        emit(ExecutionOutput.Started(startTime))

        try {
            // Crear ProcessBuilder para el shell interactivo
            val processBuilder = when (shell) {
                ShellType.POWERSHELL -> {
                    ProcessBuilder(
                        "powershell.exe",
                        "-NoLogo",
                        "-NoExit"
                    )
                }
                ShellType.CMD -> {
                    ProcessBuilder("cmd.exe")
                }
                ShellType.GIT_BASH -> {
                    ProcessBuilder("bash", "-i")
                }
                ShellType.WSL -> {
                    ProcessBuilder("wsl", "bash", "-i")
                }
            }

            // Configurar directorio de trabajo
            if (workingDirectory.isNotBlank()) {
                val workingDir = File(workingDirectory)
                if (workingDir.exists() && workingDir.isDirectory) {
                    processBuilder.directory(workingDir)
                }
            }

            processBuilder.redirectErrorStream(false)
            
            val process = withContext(Dispatchers.IO) {
                processBuilder.start()
            }
            
            // Registrar el proceso activo
            activeProcesses[processId] = process
            
            // Crear un writer para enviar comandos al proceso
            val writer = java.io.OutputStreamWriter(process.outputStream, Charsets.UTF_8)
            processWriters[processId] = writer

            // Usar canales para leer stdout y stderr en paralelo
            val stdoutChannel = Channel<String>(Channel.UNLIMITED)
            val stderrChannel = Channel<String>(Channel.UNLIMITED)

            // Lanzar coroutines para leer stdout y stderr en paralelo con soporte para REPL
            val stdoutJob = kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reader = java.io.InputStreamReader(process.inputStream, Charsets.UTF_8)
                    val lineBuffer = StringBuilder()
                    var lastFlushTime = System.currentTimeMillis()
                    
                    while (process.isAlive) {
                        if (reader.ready()) {
                            val char = reader.read()
                            if (char == -1) break
                            
                            val c = char.toChar()
                            if (c == '\n') {
                                if (lineBuffer.isNotEmpty()) {
                                    stdoutChannel.send(lineBuffer.toString())
                                    lineBuffer.clear()
                                    lastFlushTime = System.currentTimeMillis()
                                }
                            } else if (c != '\r') {
                                lineBuffer.append(c)
                            }
                        } else {
                            val now = System.currentTimeMillis()
                            if (lineBuffer.isNotEmpty() && (now - lastFlushTime) > 50) {
                                stdoutChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                                lastFlushTime = now
                            }
                            kotlinx.coroutines.delay(10)
                        }
                    }
                    
                    while (reader.ready()) {
                        val char = reader.read()
                        if (char == -1) break
                        val c = char.toChar()
                        if (c == '\n') {
                            if (lineBuffer.isNotEmpty()) {
                                stdoutChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                            }
                        } else if (c != '\r') {
                            lineBuffer.append(c)
                        }
                    }
                    
                    if (lineBuffer.isNotEmpty()) {
                        stdoutChannel.send(lineBuffer.toString())
                    }
                } catch (e: Exception) {
                    println("Error reading stdout: ${e.message}")
                } finally {
                    stdoutChannel.close()
                }
            }

            val stderrJob = kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reader = java.io.InputStreamReader(process.errorStream, Charsets.UTF_8)
                    val lineBuffer = StringBuilder()
                    var lastFlushTime = System.currentTimeMillis()
                    
                    while (process.isAlive) {
                        if (reader.ready()) {
                            val char = reader.read()
                            if (char == -1) break
                            
                            val c = char.toChar()
                            if (c == '\n') {
                                if (lineBuffer.isNotEmpty()) {
                                    stderrChannel.send(lineBuffer.toString())
                                    lineBuffer.clear()
                                    lastFlushTime = System.currentTimeMillis()
                                }
                            } else if (c != '\r') {
                                lineBuffer.append(c)
                            }
                        } else {
                            val now = System.currentTimeMillis()
                            if (lineBuffer.isNotEmpty() && (now - lastFlushTime) > 50) {
                                stderrChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                                lastFlushTime = now
                            }
                            kotlinx.coroutines.delay(10)
                        }
                    }
                    
                    while (reader.ready()) {
                        val char = reader.read()
                        if (char == -1) break
                        val c = char.toChar()
                        if (c == '\n') {
                            if (lineBuffer.isNotEmpty()) {
                                stderrChannel.send(lineBuffer.toString())
                                lineBuffer.clear()
                            }
                        } else if (c != '\r') {
                            lineBuffer.append(c)
                        }
                    }
                    
                    if (lineBuffer.isNotEmpty()) {
                        stderrChannel.send(lineBuffer.toString())
                    }
                } catch (e: Exception) {
                    println("Error reading stderr: ${e.message}")
                } finally {
                    stderrChannel.close()
                }
            }

            // Leer de ambos canales y emitir
            var stdoutClosed = false
            var stderrClosed = false

            while (!stdoutClosed || !stderrClosed) {
                select<Unit> {
                    if (!stdoutClosed) {
                        stdoutChannel.onReceiveCatching { result ->
                            result.getOrNull()?.let { line ->
                                emit(ExecutionOutput.StdOut(line))
                            } ?: run {
                                stdoutClosed = true
                            }
                        }
                    }
                    if (!stderrClosed) {
                        stderrChannel.onReceiveCatching { result ->
                            result.getOrNull()?.let { line ->
                                emit(ExecutionOutput.StdErr(line))
                            } ?: run {
                                stderrClosed = true
                            }
                        }
                    }
                }
            }

            // Esperar a que el proceso termine
            val exitCode = withContext(Dispatchers.IO) {
                stdoutJob.join()
                stderrJob.join()
                process.waitFor()
            }
            
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            // Limpiar recursos
            processWriters.remove(processId)
            activeProcesses.remove(processId)

            emit(
                ExecutionOutput.Finished(
                    exitCode = exitCode,
                    duration = duration,
                    endTime = endTime
                )
            )
        } catch (e: Exception) {
            processWriters.remove(processId)
            activeProcesses.remove(processId)
            emit(ExecutionOutput.Error(e.message ?: "Error desconocido"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Envía entrada a una terminal interactiva
     */
    override fun sendInputToTerminal(executionId: String, input: String) {
        processWriters[executionId]?.let { writer ->
            try {
                println("[CommandExecutor] Sending input to $executionId: $input")
                writer.write(input)
                writer.write("\n")
                writer.flush()
                println("[CommandExecutor] Input sent successfully")
            } catch (e: Exception) {
                println("Error sending input to terminal $executionId: ${e.message}")
                e.printStackTrace()
            }
        } ?: run {
            println("No active writer for terminal $executionId. Available: ${processWriters.keys}")
        }
    }
    
    /**
     * Envía señal de interrupción (Ctrl+C) a un proceso
     */
    override fun sendInterruptSignal(executionId: String) {
        println("[CommandExecutor] Sending interrupt signal to $executionId")
        
        activeProcesses[executionId]?.let { process ->
            try {
                if (process.isAlive) {
                    // En Windows, Ctrl+C es muy difícil de simular correctamente
                    // La mejor opción es destruir el proceso
                    println("[CommandExecutor] Destroying process for $executionId")
                    
                    // Primero intentar destruir normalmente
                    process.destroy()
                    
                    // Esperar un poco
                    Thread.sleep(100)
                    
                    // Si todavía está vivo, forzar terminación
                    if (process.isAlive) {
                        println("[CommandExecutor] Process still alive, forcing destruction")
                        process.destroyForcibly()
                    }
                    
                    println("[CommandExecutor] Process destroyed successfully")
                }
            } catch (e: Exception) {
                println("[CommandExecutor] Error sending interrupt signal to $executionId: ${e.message}")
                e.printStackTrace()
            }
        } ?: run {
            println("[CommandExecutor] No active process found for $executionId")
        }
    }
}
