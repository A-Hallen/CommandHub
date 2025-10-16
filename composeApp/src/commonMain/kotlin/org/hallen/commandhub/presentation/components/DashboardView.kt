package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.theme.useAppColors

/**
 * Vista de Dashboard con estadísticas y comandos frecuentes
 */
@Composable
fun DashboardView(
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
    
    var quickCommand by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Ejecución rápida
        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de ejecución rápida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickCommand,
                    onValueChange = { quickCommand = it },
                    placeholder = { 
                        Text(
                            "Ejecutar comando rápido...",
                            color = colors.textSecondary
                        ) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Enter) {
                                if (quickCommand.isNotBlank()) {
                                    val tempCommand = org.hallen.commandhub.domain.model.Command(
                                        id = 0L,
                                        name = "Ejecución Rápida",
                                        command = quickCommand.trim(),
                                        description = "",
                                        workingDirectory = System.getProperty("user.home") ?: "",
                                        shell = org.hallen.commandhub.domain.model.ShellType.POWERSHELL,
                                        projectId = null,
                                        categoryId = null,
                                        isFavorite = false,
                                        tags = emptyList()
                                    )
                                    onCommandExecute(tempCommand)
                                    quickCommand = ""
                                    true
                                } else {
                                    false
                                }
                            } else {
                                false
                            }
                        },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                
                IconButton(
                    onClick = {
                        if (quickCommand.isNotBlank()) {
                            val tempCommand = org.hallen.commandhub.domain.model.Command(
                                id = 0L,
                                name = "Ejecución Rápida",
                                command = quickCommand.trim(),
                                description = "",
                                workingDirectory = System.getProperty("user.home") ?: "",
                                shell = org.hallen.commandhub.domain.model.ShellType.POWERSHELL,
                                projectId = null,
                                categoryId = null,
                                isFavorite = false,
                                tags = emptyList()
                            )
                            onCommandExecute(tempCommand)
                            quickCommand = ""
                        }
                    },
                    enabled = quickCommand.isNotBlank(),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Ejecutar",
                        tint = if (quickCommand.isNotBlank()) colors.primary else colors.textSecondary
                    )
                }
            }
        }
        
        // Grid de comandos con padding para el hover
        if (commands.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No hay comandos aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            // Vista de grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(commands.take(8)) { command ->
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
}

