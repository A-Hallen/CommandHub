package org.hallen.commandhub.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
 * Card profesional de comando inspirado en inspiracion-principal.html
 */
@Composable
fun CommandCardModern(
    command: Command,
    category: Category?,
    isSelected: Boolean,
    onClick: () -> Unit,
    onExecute: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // Animaciones
    val elevation by animateFloatAsState(if (isHovered) 12.dp.value else 4.dp.value)
    val offsetY by animateFloatAsState(if (isHovered) -5f else 0f)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = offsetY.dp)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .hoverable(interactionSource)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = if (isSelected) {
            BorderStroke(2.dp, colors.primary)
        } else {
            BorderStroke(1.dp, colors.border)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Nombre
            Text(
                text = command.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Command preview (monospace)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (colors.surface == Color(0xFF151515)) {
                            // Modo oscuro: fondo ligeramente más claro
                            Color(0xFF252525)
                        } else {
                            // Modo claro: fondo gris claro
                            Color(0xFFF0F0F0)
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = command.command,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = if (colors.surface == Color(0xFF151515)) {
                        colors.textPrimary
                    } else {
                        Color(0xFF333333)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Tags (Categoría + Tags personalizados)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Categoría tag
                if (category != null) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = Color(parseHexColor(category.color)).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(parseHexColor(category.color))
                        )
                    }
                }
                
                // Tags personalizados
                command.tags.take(2).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = colors.primaryLight
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.primary
                        )
                    }
                }
                
                if (command.tags.size > 2) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = colors.primaryLight
                    ) {
                        Text(
                            text = "+${command.tags.size - 2}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.primary
                        )
                    }
                }
            }
            
            // Footer: Acciones + Botón ejecutar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Acciones de la izquierda
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (command.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorito",
                            tint = if (command.isFavorite) Color(0xFFFFD700) else colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
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
                }
                
                // Botón ejecutar
                Button(
                    onClick = onExecute,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Ejecutar",
                        fontWeight = FontWeight.Medium
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
