package org.hallen.commandhub.data.mapper

import org.hallen.commandhub.data.database.ProjectEntity
import org.hallen.commandhub.domain.model.Project

/**
 * Mappers para convertir entre ProjectEntity (DB) y Project (Domain)
 */

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        defaultWorkingDirectory = defaultWorkingDirectory,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        defaultWorkingDirectory = defaultWorkingDirectory,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
