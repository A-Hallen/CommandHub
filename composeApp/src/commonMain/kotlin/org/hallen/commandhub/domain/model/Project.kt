package org.hallen.commandhub.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa un proyecto que agrupa comandos relacionados
 */
@Serializable
data class Project(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String = "#6200EE", // Color en formato hex
    val icon: String? = null,
    val defaultWorkingDirectory: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
