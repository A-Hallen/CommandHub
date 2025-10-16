package org.hallen.commandhub.data.mapper

import org.hallen.commandhub.data.database.ExecutionEntity
import org.hallen.commandhub.domain.model.ExecutionResult
import org.hallen.commandhub.domain.model.ExecutionStatus

/**
 * Mappers para convertir entre ExecutionEntity (DB) y ExecutionResult (Domain)
 */

fun ExecutionEntity.toDomain(): ExecutionResult {
    return ExecutionResult(
        id = id,
        commandId = commandId,
        output = output,
        errorOutput = errorOutput,
        exitCode = exitCode?.toInt(),
        status = ExecutionStatus.valueOf(status),
        startTime = startTime,
        endTime = endTime,
        duration = duration
    )
}

fun ExecutionResult.toEntity(): ExecutionEntity {
    return ExecutionEntity(
        id = id,
        commandId = commandId,
        output = output,
        errorOutput = errorOutput,
        exitCode = exitCode?.toLong(),
        status = status.name,
        startTime = startTime,
        endTime = endTime,
        duration = duration
    )
}
