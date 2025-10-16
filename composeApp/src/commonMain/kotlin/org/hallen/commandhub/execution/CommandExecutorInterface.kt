package org.hallen.commandhub.execution

import kotlinx.coroutines.flow.Flow
import org.hallen.commandhub.domain.model.Command

/**
 * Interfaz común para el ejecutor de comandos
 */
interface CommandExecutorInterface {
    fun executeCommand(command: Command, executionId: String): Flow<ExecutionOutput>
    fun stopExecution(executionId: String)
    
    /**
     * Inicia una sesión de terminal interactiva
     * @param workingDirectory Directorio de trabajo inicial
     * @param shell Tipo de shell a usar
     * @return Flow de salida de la terminal
     */
    fun startInteractiveTerminal(
        workingDirectory: String = System.getProperty("user.home") ?: "",
        shell: org.hallen.commandhub.domain.model.ShellType = org.hallen.commandhub.domain.model.ShellType.POWERSHELL
    ): Flow<ExecutionOutput>
    
    /**
     * Envía un comando a una sesión de terminal interactiva activa
     * @param executionId ID de la sesión interactiva
     * @param input Comando o entrada a enviar
     */
    fun sendInputToTerminal(executionId: String, input: String)
    
    /**
     * Envía señal de interrupción (Ctrl+C) a un proceso
     * @param executionId ID de la ejecución
     */
    fun sendInterruptSignal(executionId: String)
}

/**
 * Output de ejecución (común para todas las plataformas)
 */
sealed class ExecutionOutput {
    data class Started(val startTime: Long) : ExecutionOutput()
    data class StdOut(val line: String) : ExecutionOutput()
    data class StdErr(val line: String) : ExecutionOutput()
    data class Finished(
        val exitCode: Int,
        val duration: Long,
        val endTime: Long
    ) : ExecutionOutput()
    data class Error(val message: String) : ExecutionOutput()
}
