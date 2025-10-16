package org.hallen.commandhub.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.hallen.commandhub.data.database.CommandHubDatabase
import org.hallen.commandhub.data.mapper.toDomain
import org.hallen.commandhub.data.mapper.toEntity
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val database: CommandHubDatabase
) : CategoryRepository {
    
    private val queries = database.categoryQueries
    
    override fun getAllCategories(): Flow<List<Category>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getCategoryById(id: Long): Category? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }
    
    override suspend fun insertCategory(category: Category): Long = withContext(Dispatchers.Default) {
        val entity = category.toEntity()
        queries.insert(
            name = entity.name,
            description = entity.description,
            color = entity.color,
            icon = entity.icon,
            createdAt = entity.createdAt
        )
        queries.lastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateCategory(category: Category) = withContext(Dispatchers.Default) {
        val entity = category.toEntity()
        queries.update(
            name = entity.name,
            description = entity.description,
            color = entity.color,
            icon = entity.icon,
            id = entity.id
        )
    }
    
    override suspend fun deleteCategory(id: Long) = withContext(Dispatchers.Default) {
        queries.delete(id)
    }
}
