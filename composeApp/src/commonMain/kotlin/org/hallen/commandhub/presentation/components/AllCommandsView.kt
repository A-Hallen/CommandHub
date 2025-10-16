package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.theme.useAppColors

/**
 * Vista de TODOS los comandos - Biblioteca completa
 */
@Composable
fun AllCommandsView(
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
    val colors = useAppColors()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        
        // Vista de comandos según el modo
        if (commands.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Code,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No hay comandos guardados",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Crea tu primer comando con el botón '+'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            CommandsGridView(
                commands = commands,
                categories = categories,
                selectedCommand = selectedCommand,
                onCommandSelected = onCommandSelected,
                onCommandExecute = onCommandExecute,
                onCommandEdit = onCommandEdit,
                onCommandDelete = onCommandDelete,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
