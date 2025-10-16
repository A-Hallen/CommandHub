package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.hallen.commandhub.theme.useAppColors
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.Project
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.model.ShellType

/**
 * Diálogo para crear o editar un comando
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandDialog(
    command: Command?,
    projects: List<Project>,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Command) -> Unit,
    modifier: Modifier = Modifier
) {
    // Directorio por defecto al home del usuario
    val defaultWorkingDir = remember { 
        command?.workingDirectory?.ifBlank { null } ?: System.getProperty("user.home") ?: ""
    }
    
    var name by remember { mutableStateOf(command?.name ?: "") }
    var commandText by remember { mutableStateOf(command?.command ?: "") }
    var description by remember { mutableStateOf(command?.description ?: "") }
    var workingDirectory by remember { mutableStateOf(defaultWorkingDir) }
    var selectedProjectId by remember { mutableStateOf(command?.projectId) }
    var selectedCategoryId by remember { mutableStateOf(command?.categoryId) }
    var isFavorite by remember { mutableStateOf(command?.isFavorite ?: false) }
    var tagsText by remember { mutableStateOf(command?.tags?.joinToString(", ") ?: "") }
    var selectedShell by remember { mutableStateOf(command?.shell ?: ShellType.POWERSHELL) }
    
    val isEditMode = command != null
    val title = if (isEditMode) "Editar Comando" else "Nuevo Comando"
    val colors = useAppColors()

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .width(650.dp)
                .heightIn(max = 750.dp)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header con ícono y título
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.primaryLight.copy(alpha = 0.1f))
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Filled.Edit else Icons.Filled.AddCircle,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                
                // Contenido del formulario
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        )
                    )

                    // Comando
                    OutlinedTextField(
                        value = commandText,
                        onValueChange = { commandText = it },
                        label = { Text("Comando *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        )
                    )

                    // Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        )
                    )

                    // Working Directory
                    OutlinedTextField(
                        value = workingDirectory,
                        onValueChange = { workingDirectory = it },
                        label = { Text("Directorio de trabajo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Folder,
                                contentDescription = null,
                                tint = colors.textSecondary
                            )
                        }
                    )

                    // Shell Type
                    var shellExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = shellExpanded,
                        onExpandedChange = { shellExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedShell.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Shell") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shellExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            )
                        )
                    ExposedDropdownMenu(
                        expanded = shellExpanded,
                        onDismissRequest = { shellExpanded = false }
                    ) {
                        ShellType.entries.forEach { shell ->
                            DropdownMenuItem(
                                text = { Text(shell.name) },
                                onClick = {
                                    selectedShell = shell
                                    shellExpanded = false
                                }
                            )
                        }
                    }
                }

                    // Proyecto
                    var projectExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = projectExpanded,
                        onExpandedChange = { projectExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = projects.find { it.id == selectedProjectId }?.name ?: "Ninguno",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Proyecto") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            )
                        )
                    ExposedDropdownMenu(
                        expanded = projectExpanded,
                        onDismissRequest = { projectExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguno") },
                            onClick = {
                                selectedProjectId = null
                                projectExpanded = false
                            }
                        )
                        projects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    selectedProjectId = project.id
                                    projectExpanded = false
                                }
                            )
                        }
                    }
                }

                    // Categoría
                    var categoryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = categories.find { it.id == selectedCategoryId }?.name ?: "Ninguno",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            )
                        )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguno") },
                            onClick = {
                                selectedCategoryId = null
                                categoryExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                    // Tags
                    OutlinedTextField(
                        value = tagsText,
                        onValueChange = { tagsText = it },
                        label = { Text("Tags (separados por comas)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Tag,
                                contentDescription = null,
                                tint = colors.textSecondary
                            )
                        }
                    )

                    // Favorito con diseño mejorado
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.cardBackground
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (isFavorite) colors.primary else colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Marcar como favorito",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                            Switch(
                                checked = isFavorite,
                                onCheckedChange = { isFavorite = it }
                            )
                        }
                    }
                }
                
                // Botones de acción
                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, androidx.compose.ui.Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && commandText.isNotBlank()) {
                                val tags = tagsText.split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }
                                
                                val updatedCommand = Command(
                                    id = command?.id ?: 0,
                                    name = name,
                                    command = commandText,
                                    description = description,
                                    workingDirectory = workingDirectory,
                                    projectId = selectedProjectId,
                                    categoryId = selectedCategoryId,
                                    isFavorite = isFavorite,
                                    tags = tags,
                                    shell = selectedShell,
                                    createdAt = command?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(updatedCommand)
                                onDismiss()
                            }
                        },
                        enabled = name.isNotBlank() && commandText.isNotBlank(),
                        modifier = Modifier.height(48.dp).widthIn(min = 120.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
