package org.hallen.commandhub.domain.repository

import kotlinx.coroutines.flow.Flow
import org.hallen.commandhub.domain.model.Category

/**
 * Repositorio para operaciones con categorías
 */
interface CategoryRepository {
    
    /**
     * Obtiene todas las categorías
     */
    fun getAllCategories(): Flow<List<Category>>
    
    /**
     * Obtiene una categoría por ID
     */
    suspend fun getCategoryById(id: Long): Category?
    
    /**
     * Inserta una nueva categoría
     */
    suspend fun insertCategory(category: Category): Long
    
    /**
     * Actualiza una categoría existente
     */
    suspend fun updateCategory(category: Category)
    
    /**
     * Elimina una categoría
     */
    suspend fun deleteCategory(id: Long)
}
