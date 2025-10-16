package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.Command

/**
 * Vista de comandos en grid (tarjetas)
 * Inspirado en inspiracion-principal.html con grid responsive
 */
@Composable
fun CommandsGridView(
    commands: List<Command>,
    categories: List<Category>,
    selectedCommand: Command?,
    onCommandSelected: (Command) -> Unit,
    onCommandExecute: (Command) -> Unit,
    onCommandEdit: (Command) -> Unit,
    onCommandDelete: (Command) -> Unit,
    onToggleFavorite: (Command) -> Unit,
    modifier: Modifier = Modifier
) {
    if (commands.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "No hay comandos para mostrar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(commands, key = { it.id }) { command ->
                val category = command.categoryId?.let { categoryId ->
                    categories.find { it.id == categoryId }
                }
                
                CommandCardModern(
                    command = command,
                    category = category,
                    isSelected = selectedCommand?.id == command.id,
                    onClick = { onCommandSelected(command) },
                    onExecute = { onCommandExecute(command) },
                    onEdit = { onCommandEdit(command) },
                    onDelete = { onCommandDelete(command) },
                    onToggleFavorite = { onToggleFavorite(command) }
                )
            }
        }
    }
}
