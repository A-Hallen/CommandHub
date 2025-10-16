package org.hallen.commandhub.domain.repository

import kotlinx.coroutines.flow.Flow
import org.hallen.commandhub.domain.model.Command

/**
 * Repositorio para operaciones con comandos
 */
interface CommandRepository {
    
    /**
     * Obtiene todos los comandos
     */
    fun getAllCommands(): Flow<List<Command>>
    
    /**
     * Obtiene un comando por ID
     */
    suspend fun getCommandById(id: Long): Command?
    
    /**
     * Obtiene comandos por proyecto
     */
    fun getCommandsByProject(projectId: Long): Flow<List<Command>>
    
    /**
     * Obtiene comandos por categoría
     */
    fun getCommandsByCategory(categoryId: Long): Flow<List<Command>>
    
    /**
     * Obtiene comandos favoritos
     */
    fun getFavoriteCommands(): Flow<List<Command>>
    
    /**
     * Busca comandos por nombre o descripción
     */
    fun searchCommands(query: String): Flow<List<Command>>
    
    /**
     * Inserta un nuevo comando
     */
    suspend fun insertCommand(command: Command): Long
    
    /**
     * Actualiza un comando existente
     */
    suspend fun updateCommand(command: Command)
    
    /**
     * Elimina un comando
     */
    suspend fun deleteCommand(id: Long)
    
    /**
     * Marca/desmarca un comando como favorito
     */
    suspend fun toggleFavorite(id: Long)
}
