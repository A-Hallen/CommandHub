package org.hallen.commandhub.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa una categoría para organizar comandos
 */
@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String = "#03DAC6",
    val icon: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
