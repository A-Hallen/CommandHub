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
import org.hallen.commandhub.domain.model.Project
import org.hallen.commandhub.domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val database: CommandHubDatabase
) : ProjectRepository {
    
    private val queries = database.projectQueries
    
    override fun getAllProjects(): Flow<List<Project>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getProjectById(id: Long): Project? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }
    
    override suspend fun insertProject(project: Project): Long = withContext(Dispatchers.Default) {
        val entity = project.toEntity()
        queries.insert(
            name = entity.name,
            description = entity.description,
            color = entity.color,
            icon = entity.icon,
            defaultWorkingDirectory = entity.defaultWorkingDirectory,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
        queries.lastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateProject(project: Project) = withContext(Dispatchers.Default) {
        val entity = project.toEntity()
        queries.update(
            name = entity.name,
            description = entity.description,
            color = entity.color,
            icon = entity.icon,
            defaultWorkingDirectory = entity.defaultWorkingDirectory,
            updatedAt = System.currentTimeMillis(),
            id = entity.id
        )
    }
    
    override suspend fun deleteProject(id: Long) = withContext(Dispatchers.Default) {
        queries.delete(id)
    }
}
