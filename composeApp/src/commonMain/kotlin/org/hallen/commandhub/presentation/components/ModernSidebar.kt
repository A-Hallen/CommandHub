package org.hallen.commandhub.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.theme.useAppColors

/**
 * Navegación del sidebar moderno
 */
enum class SidebarNav {
    DASHBOARD,
    COMMANDS,
    FAVORITES,
    PROJECTS,
    TERMINAL,
    SETTINGS
}

/**
 * Sidebar minimalista de 80px inspirado en inspiracion-principal.html
 */
@Composable
fun ModernSidebar(
    selectedNav: SidebarNav,
    onNavSelected: (SidebarNav) -> Unit,
    onNewCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    
    Box(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(colors.surface)
    ) {
        // Borde derecho sutil
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .align(Alignment.CenterEnd)
                .background(colors.border)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo - Cuadro redondeado con iniciales
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(0.dp)
                    .background(colors.primary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CH",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            
            Spacer(Modifier.height(30.dp))
            
            // Navegación con iconos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavItem(
                    icon = Icons.Filled.Home,
                    isSelected = selectedNav == SidebarNav.DASHBOARD,
                    onClick = { onNavSelected(SidebarNav.DASHBOARD) }
                )
                
                NavItem(
                    icon = Icons.Filled.Code,
                    isSelected = selectedNav == SidebarNav.COMMANDS,
                    onClick = { onNavSelected(SidebarNav.COMMANDS) }
                )
                
                NavItem(
                    icon = Icons.Filled.Star,
                    isSelected = selectedNav == SidebarNav.FAVORITES,
                    onClick = { onNavSelected(SidebarNav.FAVORITES) }
                )
                
                NavItem(
                    icon = Icons.Filled.Folder,
                    isSelected = selectedNav == SidebarNav.PROJECTS,
                    onClick = { onNavSelected(SidebarNav.PROJECTS) }
                )
                
                NavItem(
                    icon = Icons.Filled.Terminal,
                    isSelected = selectedNav == SidebarNav.TERMINAL,
                    onClick = { onNavSelected(SidebarNav.TERMINAL) }
                )
                
                NavItem(
                    icon = Icons.Filled.Settings,
                    isSelected = selectedNav == SidebarNav.SETTINGS,
                    onClick = { onNavSelected(SidebarNav.SETTINGS) }
                )
            }
            
            // Botón flotante circular + para nuevo comando
            val addButtonInteractionSource = remember { MutableInteractionSource() }
            val isAddButtonHovered by addButtonInteractionSource.collectIsHoveredAsState()
            val addButtonScale by animateFloatAsState(if (isAddButtonHovered) 1.1f else 1.0f)
            
            FloatingActionButton(
                onClick = onNewCommand,
                modifier = Modifier
                    .size(48.dp)
                    .scale(addButtonScale)
                    .shadow(
                        elevation = if (isAddButtonHovered) 12.dp else 6.dp,
                        shape = CircleShape
                    )
                    .hoverable(addButtonInteractionSource),
                shape = CircleShape,
                containerColor = colors.primary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    hoveredElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nuevo Comando",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // Animaciones
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> colors.primary
            isHovered -> colors.primaryLight.copy(alpha = 0.1f)
            else -> Color.Transparent
        }
    )
    
    val iconColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isHovered -> colors.primary
            else -> colors.textSecondary
        }
    )
    
    val scale by animateFloatAsState(if (isHovered && !isSelected) 1.05f else 1.0f)
    
    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
