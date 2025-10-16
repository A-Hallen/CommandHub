package org.hallen.commandhub.domain.repository

import kotlinx.coroutines.flow.Flow
import org.hallen.commandhub.domain.model.Project

/**
 * Repositorio para operaciones con proyectos
 */
interface ProjectRepository {
    
    /**
     * Obtiene todos los proyectos
     */
    fun getAllProjects(): Flow<List<Project>>
    
    /**
     * Obtiene un proyecto por ID
     */
    suspend fun getProjectById(id: Long): Project?
    
    /**
     * Inserta un nuevo proyecto
     */
    suspend fun insertProject(project: Project): Long
    
    /**
     * Actualiza un proyecto existente
     */
    suspend fun updateProject(project: Project)
    
    /**
     * Elimina un proyecto
     */
    suspend fun deleteProject(id: Long)
}
