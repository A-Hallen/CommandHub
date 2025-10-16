package org.hallen.commandhub.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.theme.useAppColors

/**
 * Vista de lista moderna de comandos (table-like)
 */
@Composable
fun CommandListModern(
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
    
    if (commands.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay comandos para mostrar",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            // Header de la tabla
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nombre",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = "Comando",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(0.35f)
                    )
                    Text(
                        text = "Categoría",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(0.15f)
                    )
                    Text(
                        text = "Acciones",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(0.25f)
                    )
                }
            }
            
            // Lista de comandos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(commands, key = { it.id }) { command ->
                    val category = command.categoryId?.let { categoryId ->
                        categories.find { it.id == categoryId }
                    }
                    
                    CommandListItem(
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

@Composable
private fun CommandListItem(
    command: Command,
    category: Category?,
    isSelected: Boolean,
    onClick: () -> Unit,
    onExecute: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val colors = useAppColors()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val backgroundColor by animateColorAsState(
        when {
            isSelected -> colors.primaryLight.copy(alpha = 0.15f)
            isHovered -> colors.surface
            else -> Color.Transparent
        }
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, colors.primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nombre del comando
            Row(
                modifier = Modifier.weight(0.25f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (command.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Favorito",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = command.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Comando (monospace)
            Text(
                text = command.command,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.35f)
            )
            
            // Categoría
            Box(modifier = Modifier.weight(0.15f)) {
                if (category != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(parseHexColor(category.color)).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(parseHexColor(category.color)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // Acciones
            Row(
                modifier = Modifier.weight(0.25f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorito
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (command.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorito",
                        tint = if (command.isFavorite) Color(0xFFFFD700) else colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Editar
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Eliminar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Ejecutar
                Button(
                    onClick = onExecute,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Convierte un color hexadecimal a Long para Color
 */
private fun parseHexColor(hex: String): Long {
    val cleanHex = hex.removePrefix("#")
    return when (cleanHex.length) {
        6 -> ("FF" + cleanHex).toLong(16)
        8 -> cleanHex.toLong(16)
        else -> 0xFF2A68FF // Fallback al azul primary
    }
}
