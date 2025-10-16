package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.Project
import org.hallen.commandhub.theme.useAppColors

/**
 * Vista de Proyectos con comandos agrupados
 */
@Composable
fun ProjectsView(
    commands: List<Command>,
    projects: List<Project>,
    categories: List<Category>,
    selectedCommand: Command?,
    onCommandSelected: (Command) -> Unit,
    onCommandExecute: (Command) -> Unit,
    onCommandEdit: (Command) -> Unit,
    onCommandDelete: (Command) -> Unit,
    onToggleFavorite: (Command) -> Unit,
    onProjectAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    var selectedProject by remember { mutableStateOf<Project?>(projects.firstOrNull()) }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header con botón para agregar proyecto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Proyectos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "${projects.size} proyecto${if (projects.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            
            Button(
                onClick = onProjectAdd,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                modifier = Modifier.height(42.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Añadir Proyecto")
            }
        }
        
        if (projects.isEmpty()) {
            // Estado vacío
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
                        imageVector = Icons.Filled.Folder,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No hay proyectos aún",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Crea un proyecto para organizar tus comandos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            // Layout de dos columnas
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Columna izquierda: Lista de proyectos
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Proyectos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(projects) { project ->
                            val projectCommands = commands.filter { it.projectId == project.id }
                            val isSelected = selectedProject?.id == project.id
                            val isFirst = projects.indexOf(project) == 0
                            
                            ProjectCard(
                                project = project,
                                commandCount = projectCommands.size,
                                isSelected = isSelected,
                                isHighlighted = isFirst,
                                colors = colors,
                                onClick = { selectedProject = project }
                            )
                        }
                    }
                }
                
                // Columna derecha: Comandos del proyecto seleccionado
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    selectedProject?.let { project ->
                        Text(
                            text = "Comandos de ${project.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val projectCommands = commands.filter { it.projectId == project.id }
                        
                        if (projectCommands.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay comandos en este proyecto",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(projectCommands) { command ->
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
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    commandCount: Int,
    isSelected: Boolean,
    isHighlighted: Boolean,
    colors: org.hallen.commandhub.theme.AppColors,
    onClick: () -> Unit
) {
    val blueHighlight = Color(0xFF5865F2) // Color azul como en la imagen
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = when {
            isHighlighted -> blueHighlight
            else -> colors.cardBackground
        },
        shadowElevation = if (isHighlighted) 4.dp else 2.dp,
        border = if (isSelected && !isHighlighted) BorderStroke(2.dp, colors.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted) Color.White else colors.textPrimary
                )
                if (project.description.isNotEmpty()) {
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isHighlighted) Color.White.copy(alpha = 0.9f) else colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Botón con ícono +
            if (isHighlighted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colors.primary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$commandCount",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

