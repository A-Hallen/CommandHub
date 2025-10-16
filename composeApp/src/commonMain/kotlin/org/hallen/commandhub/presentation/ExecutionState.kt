package org.hallen.commandhub.presentation

import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.ExecutionStatus

/**
 * Estado de una ejecución de comando
 */
data class ExecutionState(
    val command: Command,
    val status: ExecutionStatus,
    val output: String = "",
    val errorOutput: String = "",
    val exitCode: Int? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long? = null,
    val isInteractive: Boolean = true  // Todas las ejecuciones son interactivas por defecto
) {
    val id: String = "${command.id}_$startTime"
}
