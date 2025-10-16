package org.hallen.commandhub.data.mapper

import kotlinx.serialization.json.Json
import org.hallen.commandhub.data.database.CommandEntity
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.ShellType

/**
 * Mappers para convertir entre CommandEntity (DB) y Command (Domain)
 */

fun CommandEntity.toDomain(): Command {
    return Command(
        id = id,
        name = name,
        command = command,
        description = description,
        workingDirectory = workingDirectory,
        projectId = projectId,
        categoryId = categoryId,
        isFavorite = isFavorite == 1L,
        tags = if (tags.isNotEmpty()) Json.decodeFromString<List<String>>(tags) else emptyList(),
        shell = ShellType.valueOf(shell),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Command.toEntity(): CommandEntity {
    return CommandEntity(
        id = id,
        name = name,
        command = command,
        description = description,
        workingDirectory = workingDirectory,
        projectId = projectId,
        categoryId = categoryId,
        isFavorite = if (isFavorite) 1L else 0L,
        tags = if (tags.isNotEmpty()) Json.encodeToString(tags) else "",
        shell = shell.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
