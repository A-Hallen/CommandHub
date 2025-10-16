package org.hallen.commandhub.data.mapper

import org.hallen.commandhub.data.database.CategoryEntity
import org.hallen.commandhub.domain.model.Category

/**
 * Mappers para convertir entre CategoryEntity (DB) y Category (Domain)
 */

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        createdAt = createdAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        createdAt = createdAt
    )
}
