package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.theme.useThemeManager
import org.hallen.commandhub.theme.ThemeMode
import org.hallen.commandhub.theme.useAppColors

/**
 * Barra de herramientas superior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandToolbar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSection: String = "CommandHub",
    currentSectionSubtitle: String = "",
    modifier: Modifier = Modifier
) {
    val themeManager = useThemeManager()
    val isDarkMode = themeManager.themeMode.value == ThemeMode.DARK
    val colors = useAppColors()
    
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = currentSection,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    if (currentSectionSubtitle.isNotEmpty()) {
                        Text(
                            text = currentSectionSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search bar con diseño mejorado
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { 
                            Text(
                                "Buscar comandos...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            ) 
                        },
                        modifier = Modifier
                            .width(400.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedContainerColor = colors.cardBackground.copy(alpha = 0.5f),
                            unfocusedContainerColor = colors.cardBackground.copy(alpha = 0.3f)
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search, 
                                contentDescription = "Buscar",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    
                    // Theme toggle button
                    IconButton(
                        onClick = { themeManager.toggleTheme() }
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Cambiar tema",
                            tint = colors.textSecondary
                        )
                    }
                }
            }
        }
    )
}
