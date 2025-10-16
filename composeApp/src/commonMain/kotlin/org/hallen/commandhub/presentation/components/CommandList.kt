package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.Category

/**
 * Lista de comandos
 */
@Composable
fun CommandList(
    commands: List<Command>,
    categories: List<Category>,
    selectedCommand: Command?,
    onCommandSelected: (Command) -> Unit,
    onCommandExecute: (Command) -> Unit,
    onCommandEdit: (Command) -> Unit,
    onCommandDelete: (Command) -> Unit,
    modifier: Modifier = Modifier
) {
    if (commands.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔍",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "No se encontraron comandos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(commands, key = { it.id }) { command ->
                val category = command.categoryId?.let { categoryId ->
                    categories.find { it.id == categoryId }
                }
                CommandCard(
                    command = command,
                    category = category,
                    isSelected = selectedCommand?.id == command.id,
                    onClick = { onCommandSelected(command) },
                    onExecute = { onCommandExecute(command) },
                    onEdit = { onCommandEdit(command) },
                    onDelete = { onCommandDelete(command) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommandCard(
    command: Command,
    category: Category?,
    isSelected: Boolean,
    onClick: () -> Unit,
    onExecute: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (command.isFavorite) "⭐ ${command.name}" else command.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    // Badge de categoría con color
                    if (category != null) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color(parseHexColor(category.color))
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (command.description.isNotEmpty()) {
                    Text(
                        text = command.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = command.command,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tags
                if (command.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        command.tags.take(3).forEach { tag ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (command.tags.size > 3) {
                            Text(
                                text = "+${command.tags.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Execute button
                Button(
                    onClick = onExecute
                ) {
                    Text("▶")
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("✏️", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("🗑️", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

/**
 * Convierte un color hexadecimal (ej: "#2196F3") a un Long para Color
 */
private fun parseHexColor(hex: String): Long {
    val cleanHex = hex.removePrefix("#")
    return when (cleanHex.length) {
        6 -> ("FF" + cleanHex).toLong(16)
        8 -> cleanHex.toLong(16)
        else -> 0xFF000000
    }
}
