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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import org.hallen.commandhub.domain.model.Project
import org.hallen.commandhub.domain.model.Category

/**
 * Sidebar con navegación por proyectos y categorías
 */
@Composable
fun Sidebar(
    projects: List<Project>,
    categories: List<Category>,
    selectedProjectId: Long?,
    selectedCategoryId: Long?,
    showFavoritesOnly: Boolean,
    onProjectSelected: (Long?) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onShowFavoritesChange: (Boolean) -> Unit,
    onCreateProject: () -> Unit,
    onEditProject: (Project) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onCreateCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Todos los comandos
            item {
                SidebarItem(
                    label = "Todos",
                    isSelected = selectedProjectId == null && selectedCategoryId == null && !showFavoritesOnly,
                    onClick = {
                        onProjectSelected(null)
                        onCategorySelected(null)
                        onShowFavoritesChange(false)
                    }
                )
            }

            // Favoritos
            item {
                SidebarItem(
                    label = "⭐ Favoritos",
                    isSelected = showFavoritesOnly,
                    onClick = {
                        onShowFavoritesChange(!showFavoritesOnly)
                        onProjectSelected(null)
                        onCategorySelected(null)
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Proyectos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROYECTOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onCreateProject,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("+", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            items(projects) { project ->
                SidebarItemWithActions(
                    label = "📁 ${project.name}",
                    isSelected = selectedProjectId == project.id,
                    onClick = {
                        onProjectSelected(if (selectedProjectId == project.id) null else project.id)
                        onCategorySelected(null)
                        onShowFavoritesChange(false)
                    },
                    onEdit = { onEditProject(project) },
                    onDelete = { onDeleteProject(project.id) }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Categorías
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CATEGORÍAS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onCreateCategory,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("+", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            items(categories) { category ->
                CategorySidebarItem(
                    category = category,
                    isSelected = selectedCategoryId == category.id,
                    onClick = {
                        onCategorySelected(if (selectedCategoryId == category.id) null else category.id)
                        onProjectSelected(null)
                        onShowFavoritesChange(false)
                    },
                    onEdit = { onEditCategory(category) },
                    onDelete = { onDeleteCategory(category.id) }
                )
            }
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}

@Composable
private fun SidebarItemWithActions(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { 
                    onEdit()
                },
                modifier = Modifier.size(20.dp)
            ) {
                Text("✏️", style = MaterialTheme.typography.labelSmall)
            }
            
            IconButton(
                onClick = {
                    onDelete()
                },
                modifier = Modifier.size(20.dp)
            ) {
                Text("🗑️", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun CategorySidebarItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo de color de la categoría
            Surface(
                modifier = Modifier.size(12.dp),
                shape = CircleShape,
                color = Color(parseHexColor(category.color))
            ) {}
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { 
                    onEdit()
                },
                modifier = Modifier.size(20.dp)
            ) {
                Text("✏️", style = MaterialTheme.typography.labelSmall)
            }
            
            IconButton(
                onClick = {
                    onDelete()
                },
                modifier = Modifier.size(20.dp)
            ) {
                Text("🗑️", style = MaterialTheme.typography.labelSmall)
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
