package org.hallen.commandhub.domain.repository

import kotlinx.coroutines.flow.Flow
import org.hallen.commandhub.domain.model.ExecutionResult

/**
 * Repositorio para el historial de ejecuciones
 */
interface ExecutionRepository {
    
    /**
     * Obtiene el historial de ejecuciones de un comando
     */
    fun getExecutionHistory(commandId: Long): Flow<List<ExecutionResult>>
    
    /**
     * Obtiene una ejecución por ID
     */
    suspend fun getExecutionById(id: Long): ExecutionResult?
    
    /**
     * Inserta un nuevo resultado de ejecución
     */
    suspend fun insertExecution(execution: ExecutionResult): Long
    
    /**
     * Actualiza un resultado de ejecución
     */
    suspend fun updateExecution(execution: ExecutionResult)
    
    /**
     * Elimina el historial de un comando
     */
    suspend fun deleteExecutionHistory(commandId: Long)
    
    /**
     * Elimina todo el historial más antiguo que X días
     */
    suspend fun cleanOldHistory(daysToKeep: Int)
}
