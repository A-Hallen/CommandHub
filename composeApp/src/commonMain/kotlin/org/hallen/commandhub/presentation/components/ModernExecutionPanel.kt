package org.hallen.commandhub.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.domain.model.ExecutionStatus
import org.hallen.commandhub.presentation.ExecutionState
import org.hallen.commandhub.theme.useAppColors
import org.hallen.commandhub.terminal.AnsiText
import org.hallen.commandhub.terminal.useTerminalSettings
import org.hallen.commandhub.terminal.AnsiColorMapper

/**
 * Panel moderno de ejecución de comandos con controles
 * Inspirado en terminales profesionales
 */
@Composable
fun ModernExecutionPanel(
    executions: Map<String, ExecutionState>,
    selectedExecutionId: String?,
    onExecutionSelected: (String) -> Unit,
    onCloseExecution: (String) -> Unit,
    onStopExecution: (String) -> Unit,
    onSendCommand: (executionId: String, command: String) -> Unit = { _, _ -> },
    onSendInterrupt: (executionId: String) -> Unit = {},
    onNewInteractiveTerminal: (() -> Unit)? = null,
    isCollapsible: Boolean = true,
    initialExpanded: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    var isExpanded by remember { mutableStateOf(initialExpanded) }
    
    Column(
        modifier = modifier.then(
            if (isCollapsible && !isExpanded) {
                Modifier.fillMaxWidth().height(48.dp) // Solo header cuando está contraído
            } else {
                Modifier.fillMaxSize()
            }
        )
    ) {
        // Header horizontal con tabs integrados
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Izquierda: Terminal + Badge + Tabs
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Terminal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        
                        // Badge esférico centrado
                        if (executions.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .aspectRatio(1f)
                                    .background(
                                        color = colors.primary,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = executions.size.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    // Tabs compactos (si hay ejecuciones y está expandido)
                    if (executions.isNotEmpty() && isExpanded) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            executions.values.take(3).forEach { execution ->
                                CompactExecutionTab(
                                    execution = execution,
                                    isSelected = execution.id == selectedExecutionId,
                                    onClick = { onExecutionSelected(execution.id) },
                                    onClose = { onCloseExecution(execution.id) }
                                )
                            }
                            if (executions.size > 3) {
                                Text(
                                    text = "+${executions.size - 3}",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
                
                // Derecha: Botones de acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón para nueva terminal interactiva (solo en modo no colapsible)
                    if (!isCollapsible && onNewInteractiveTerminal != null) {
                        FilledTonalButton(
                            onClick = onNewInteractiveTerminal,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Nueva Terminal",
                                fontSize = 13.sp
                            )
                        }
                    }
                    
                    // Botón contraer (solo si es colapsible)
                    if (isCollapsible) {
                        IconButton(
                            onClick = { 
                                isExpanded = !isExpanded
                                onExpandedChange?.invoke(isExpanded)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = if (isExpanded) "Contraer" else "Expandir",
                                tint = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
        
        // Contenido (expandible)
        if (isExpanded) {
            if (executions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No hay ejecuciones activas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "Los comandos que ejecutes aparecerán aquí",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            } else {
                // Contenido de la ejecución seleccionada
                val validSelectedId = if (selectedExecutionId != null && executions.containsKey(selectedExecutionId)) {
                    selectedExecutionId
                } else {
                    executions.keys.firstOrNull()
                }
                
                Column(modifier = Modifier.fillMaxSize()) {
                    validSelectedId?.let { id ->
                        executions[id]?.let { execution ->
                            ExecutionContent(
                                execution = execution,
                                onStop = { onStopExecution(execution.id) },
                                onSendCommand = { cmd -> onSendCommand(execution.id, cmd) },
                                onSendInterrupt = { onSendInterrupt(execution.id) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactExecutionTab(
    execution: ExecutionState,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val colors = useAppColors()
    
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        color = if (isSelected) colors.primaryLight else Color.Transparent,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, colors.primary)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = when (execution.status) {
                            ExecutionStatus.RUNNING -> colors.primary
                            ExecutionStatus.COMPLETED -> colors.success
                            ExecutionStatus.FAILED -> colors.danger
                            ExecutionStatus.STOPPED -> colors.warning
                            ExecutionStatus.TIMEOUT -> colors.warning
                        },
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            
            Text(
                text = execution.command.name,
                fontSize = 12.sp,
                maxLines = 1,
                color = if (isSelected) colors.primary else colors.textPrimary
            )
            
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ExecutionContent(
    execution: ExecutionState,
    onStop: () -> Unit,
    onSendCommand: (String) -> Unit = {},
    onSendInterrupt: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    val terminalSettings = useTerminalSettings()
    val scrollState = rememberScrollState()
    
    // Auto-scroll al final cuando hay nuevo output (si está habilitado)
    LaunchedEffect(execution.output, terminalSettings.autoScroll) {
        if (terminalSettings.autoScroll) {
            if (terminalSettings.smoothScroll) {
                scrollState.animateScrollTo(scrollState.maxValue)
            } else {
                scrollState.scrollTo(scrollState.maxValue)
            }
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Barra de controles
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.cardBackground,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info del comando
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status badge
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        color = when (execution.status) {
                            ExecutionStatus.RUNNING -> colors.primary.copy(alpha = 0.2f)
                            ExecutionStatus.COMPLETED -> colors.success.copy(alpha = 0.2f)
                            ExecutionStatus.FAILED -> colors.danger.copy(alpha = 0.2f)
                            ExecutionStatus.STOPPED -> colors.warning.copy(alpha = 0.2f)
                            ExecutionStatus.TIMEOUT -> colors.warning.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = when (execution.status) {
                                ExecutionStatus.RUNNING -> "Ejecutando..."
                                ExecutionStatus.COMPLETED -> "Completado"
                                ExecutionStatus.FAILED -> "Error"
                                ExecutionStatus.STOPPED -> "Detenido"
                                ExecutionStatus.TIMEOUT -> "Timeout"
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (execution.status) {
                                ExecutionStatus.RUNNING -> colors.primary
                                ExecutionStatus.COMPLETED -> colors.success
                                ExecutionStatus.FAILED -> colors.danger
                                ExecutionStatus.STOPPED -> colors.warning
                                ExecutionStatus.TIMEOUT -> colors.warning
                            }
                        )
                    }
                    
                    // Exit code (si terminó)
                    if (execution.status != ExecutionStatus.RUNNING && execution.exitCode != null) {
                        Text(
                            text = "Código de salida: ${execution.exitCode}",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
                
                // Controles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Botón stop (solo si está corriendo)
                    if (execution.status == ExecutionStatus.RUNNING) {
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Detener",
                                tint = colors.danger,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Output del terminal con soporte ANSI
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            color = AnsiColorMapper.getTerminalBackground()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                if (execution.output.isEmpty()) {
                    Text(
                        text = "Esperando salida...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = terminalSettings.fontSize.sp,
                        color = AnsiColorMapper.getDefaultTextColor().copy(alpha = 0.5f),
                        lineHeight = terminalSettings.lineHeight.sp
                    )
                } else {
                    AnsiText(
                        text = execution.output
                        // Las configuraciones se aplican automáticamente desde useTerminalSettings()
                    )
                }
            }
        }
        
        // Input interactivo (siempre que el proceso esté corriendo)
        if (execution.status == ExecutionStatus.RUNNING) {
            InteractiveTerminalInput(
                onSendCommand = onSendCommand,
                onSendInterrupt = onSendInterrupt,
                isProcessRunning = true
            )
        }
    }
}
