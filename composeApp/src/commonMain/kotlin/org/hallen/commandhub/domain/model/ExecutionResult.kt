package org.hallen.commandhub.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa el resultado de la ejecución de un comando
 */
@Serializable
data class ExecutionResult(
    val id: Long = 0,
    val commandId: Long,
    val output: String = "",
    val errorOutput: String = "",
    val exitCode: Int? = null,
    val status: ExecutionStatus = ExecutionStatus.RUNNING,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val duration: Long? = null // en milisegundos
)

/**
 * Estados posibles de una ejecución
 */
@Serializable
enum class ExecutionStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED,
    TIMEOUT
}

/**
 * Representa una línea de output en tiempo real
 */
data class OutputLine(
    val text: String,
    val type: OutputType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OutputType {
    STDOUT,
    STDERR,
    SYSTEM
}
