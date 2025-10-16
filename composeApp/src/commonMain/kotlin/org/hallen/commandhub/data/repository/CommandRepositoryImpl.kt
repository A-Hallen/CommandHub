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
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.repository.CommandRepository

class CommandRepositoryImpl(
    private val database: CommandHubDatabase
) : CommandRepository {
    
    private val queries = database.commandQueries
    
    override fun getAllCommands(): Flow<List<Command>> {
        println("[CommandRepo] getAllCommands() llamado")
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> 
                println("[CommandRepo] getAllCommands: Recibidos ${entities.size} comandos desde la base de datos")
                entities.map { it.toDomain() } 
            }
    }
    
    override suspend fun getCommandById(id: Long): Command? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }
    
    override fun getCommandsByProject(projectId: Long): Flow<List<Command>> {
        return queries.selectByProject(projectId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getCommandsByCategory(categoryId: Long): Flow<List<Command>> {
        return queries.selectByCategory(categoryId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getFavoriteCommands(): Flow<List<Command>> {
        return queries.selectFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun searchCommands(query: String): Flow<List<Command>> {
        return queries.search(query, query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertCommand(command: Command): Long = withContext(Dispatchers.Default) {
        try {
            println("[CommandRepo] insertCommand() - Insertando comando: ${command.name}")
            val entity = command.toEntity()
            queries.insert(
                name = entity.name,
                command = entity.command,
                description = entity.description,
                workingDirectory = entity.workingDirectory,
                projectId = entity.projectId,
                categoryId = entity.categoryId,
                isFavorite = entity.isFavorite,
                tags = entity.tags,
                shell = entity.shell,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
            val insertedId = queries.lastInsertRowId().executeAsOne()
            println("[CommandRepo] insertCommand() - Comando insertado con ID: $insertedId")
            return@withContext insertedId
        } catch (e: Exception) {
            println("[CommandRepo] ERROR insertCommand(): ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    override suspend fun updateCommand(command: Command) = withContext(Dispatchers.Default) {
        try {
            println("[CommandRepo] updateCommand() - Actualizando comando ID: ${command.id}, nombre: ${command.name}")
            val entity = command.toEntity()
            queries.update(
                name = entity.name,
                command = entity.command,
                description = entity.description,
                workingDirectory = entity.workingDirectory,
                projectId = entity.projectId,
                categoryId = entity.categoryId,
                isFavorite = entity.isFavorite,
                tags = entity.tags,
                shell = entity.shell,
                updatedAt = System.currentTimeMillis(),
                id = entity.id
            )
            println("[CommandRepo] updateCommand() - Comando actualizado exitosamente")
        } catch (e: Exception) {
            println("[CommandRepo] ERROR updateCommand(): ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    override suspend fun deleteCommand(id: Long) = withContext(Dispatchers.Default) {
        try {
            println("[CommandRepo] deleteCommand() - Eliminando comando ID: $id")
            queries.delete(id)
            println("[CommandRepo] deleteCommand() - Comando eliminado exitosamente")
        } catch (e: Exception) {
            println("[CommandRepo] ERROR deleteCommand(): ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    override suspend fun toggleFavorite(id: Long) = withContext(Dispatchers.Default) {
        queries.toggleFavorite(System.currentTimeMillis(), id)
    }
}
