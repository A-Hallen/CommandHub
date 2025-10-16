package org.hallen.commandhub.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa un comando guardado por el usuario
 */
@Serializable
data class Command(
    val id: Long = 0,
    val name: String,
    val command: String,
    val description: String = "",
    val workingDirectory: String = "",
    val projectId: Long? = null,
    val categoryId: Long? = null,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val shell: ShellType = ShellType.POWERSHELL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Tipos de shell soportados
 */
@Serializable
enum class ShellType {
    POWERSHELL,
    CMD,
    GIT_BASH,
    WSL
}
