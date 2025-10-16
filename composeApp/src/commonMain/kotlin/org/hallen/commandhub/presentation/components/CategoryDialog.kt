package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.Category

/**
 * Diálogo para crear o editar una categoría
 */
@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var color by remember { mutableStateOf(category?.color ?: "#2196F3") }
    var nameError by remember { mutableStateOf(false) }
    
    // Colores predefinidos
    val predefinedColors = listOf(
        "#2196F3" to "Azul",
        "#4CAF50" to "Verde",
        "#FF9800" to "Naranja",
        "#F44336" to "Rojo",
        "#9C27B0" to "Púrpura",
        "#00BCD4" to "Cyan",
        "#FFEB3B" to "Amarillo",
        "#795548" to "Marrón"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (category == null) "Nueva Categoría" else "Editar Categoría")
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
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium
                )
                
                // Grid de colores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    predefinedColors.chunked(4).forEach { rowColors ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowColors.forEach { (colorHex, _) ->
                                val isSelected = colorHex == color
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = MaterialTheme.shapes.small,
                                    color = Color(parseHexColor(colorHex)),
                                    border = if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(
                                            3.dp,
                                            MaterialTheme.colorScheme.primary
                                        )
                                    } else null,
                                    onClick = { color = colorHex }
                                ) {}
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Category(
                                id = category?.id ?: 0L,
                                name = name.trim(),
                                color = color
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
