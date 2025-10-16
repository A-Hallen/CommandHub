package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Project

/**
 * Diálogo para crear o editar un proyecto
 */
@Composable
fun ProjectDialog(
    project: Project?,
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    var name by remember { mutableStateOf(project?.name ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }
    var nameError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (project == null) "Nuevo Proyecto" else "Editar Proyecto")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Nombre *") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("El nombre es requerido") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Project(
                                id = project?.id ?: 0L,
                                name = name.trim(),
                                description = description.trim()
                            )
                        )
                        onDismiss()
                    } else {
                        nameError = true
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
