package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.theme.ThemeMode
import org.hallen.commandhub.theme.useAppColors
import org.hallen.commandhub.theme.useThemeManager
import org.hallen.commandhub.terminal.rememberTerminalSettingsManager

/**
 * Vista de configuración de la aplicación
 */
@Composable
fun SettingsView(
    categories: List<Category> = emptyList(),
    onCategoryAdd: () -> Unit = {},
    onCategoryEdit: (Category) -> Unit = {},
    onCategoryDelete: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    val themeManager = useThemeManager()
    val currentTheme = themeManager.themeMode.value
    val terminalSettingsManager = rememberTerminalSettingsManager()
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Sección: Apariencia
        item {
            SettingsSection(title = "Apariencia") {
                SettingItem(
                    icon = Icons.Filled.Palette,
                    title = "Tema",
                    description = "Selecciona el tema de la aplicación"
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = currentTheme == ThemeMode.LIGHT,
                            onClick = { themeManager.setTheme(ThemeMode.LIGHT) },
                            label = { Text("Claro") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        FilterChip(
                            selected = currentTheme == ThemeMode.DARK,
                            onClick = { themeManager.setTheme(ThemeMode.DARK) },
                            label = { Text("Oscuro") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DarkMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
        
        // Sección: Terminal (Completa)
        item {
            TerminalSettingsSection(
                settingsManager = terminalSettingsManager
            )
        }
        
        // Sección: Comandos
        item {
            SettingsSection(title = "Comandos") {
                var showNotifications by remember { mutableStateOf(true) }
                
                SettingItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones",
                    description = "Mostrar notificaciones cuando un comando termina"
                ) {
                    Switch(
                        checked = showNotifications,
                        onCheckedChange = { showNotifications = it }
                    )
                }
            }
        }
        
        // Sección: Categorías
        item {
            SettingsSection(title = "Categorías") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón agregar categoría
                    Button(
                        onClick = onCategoryAdd,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Nueva Categoría")
                    }
                    
                    // Lista de categorías
                    if (categories.isEmpty()) {
                        Text(
                            text = "No hay categorías creadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        categories.forEach { category ->
                            CategoryItem(
                                category = category,
                                onEdit = { onCategoryEdit(category) },
                                onDelete = { onCategoryDelete(category.id) }
                            )
                        }
                    }
                }
            }
        }
        
        // Sección: Atajos de teclado
        item {
            SettingsSection(title = "Atajos de teclado") {
                KeyboardShortcutItem(
                    action = "Nuevo comando",
                    shortcut = "Ctrl+N"
                )
                KeyboardShortcutItem(
                    action = "Buscar",
                    shortcut = "Ctrl+F"
                )
                KeyboardShortcutItem(
                    action = "Ejecutar comando seleccionado",
                    shortcut = "Enter"
                )
                KeyboardShortcutItem(
                    action = "Abrir terminal",
                    shortcut = "Ctrl+T"
                )
            }
        }
        
        // Sección: Diagnóstico
        item {
            SettingsSection(title = "Diagnóstico y Logs") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Directorio de datos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Ubicación:",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "%USERPROFILE%\\.commandhub",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Text(
                        text = "Los logs se guardan en: commandhub.log",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    
                    Text(
                        text = "La base de datos se encuentra en: commandhub.db",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
        
        // Sección: Acerca de
        item {
            SettingsSection(title = "Acerca de") {
                AboutItem(
                    label = "Versión",
                    value = "1.0.0"
                )
                AboutItem(
                    label = "Desarrollador",
                    value = "Adrian H"
                )
                AboutItem(
                    label = "Licencia",
                    value = "MIT"
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = useAppColors()
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.cardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    control: @Composable () -> Unit
) {
    val colors = useAppColors()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = colors.primary.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        
        control()
    }
}

@Composable
private fun KeyboardShortcutItem(
    action: String,
    shortcut: String
) {
    val colors = useAppColors()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = action,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
        
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
        ) {
            Text(
                text = shortcut,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = useAppColors()
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(category.color.toLongOrNull(16) ?: 0xFF6366F1),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
                
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                    if (category.description.isNotEmpty()) {
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                        tint = colors.danger,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutItem(
    label: String,
    value: String
) {
    val colors = useAppColors()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}
