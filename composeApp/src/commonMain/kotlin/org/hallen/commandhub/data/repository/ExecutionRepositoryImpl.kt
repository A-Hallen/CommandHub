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
import org.hallen.commandhub.domain.model.ExecutionResult
import org.hallen.commandhub.domain.repository.ExecutionRepository

class ExecutionRepositoryImpl(
    private val database: CommandHubDatabase
) : ExecutionRepository {
    
    private val queries = database.executionQueries
    
    override fun getExecutionHistory(commandId: Long): Flow<List<ExecutionResult>> {
        return queries.selectByCommand(commandId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getExecutionById(id: Long): ExecutionResult? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }
    
    override suspend fun insertExecution(execution: ExecutionResult): Long = withContext(Dispatchers.Default) {
        val entity = execution.toEntity()
        queries.insert(
            commandId = entity.commandId,
            output = entity.output,
            errorOutput = entity.errorOutput,
            exitCode = entity.exitCode,
            status = entity.status,
            startTime = entity.startTime,
            endTime = entity.endTime,
            duration = entity.duration
        )
        queries.lastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateExecution(execution: ExecutionResult) = withContext(Dispatchers.Default) {
        val entity = execution.toEntity()
        queries.update(
            output = entity.output,
            errorOutput = entity.errorOutput,
            exitCode = entity.exitCode,
            status = entity.status,
            endTime = entity.endTime,
            duration = entity.duration,
            id = entity.id
        )
    }
    
    override suspend fun deleteExecutionHistory(commandId: Long) = withContext(Dispatchers.Default) {
        queries.deleteByCommand(commandId)
    }
    
    override suspend fun cleanOldHistory(daysToKeep: Int) = withContext(Dispatchers.Default) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        queries.deleteOldHistory(cutoffTime)
    }
}
