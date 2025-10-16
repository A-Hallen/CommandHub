package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hallen.commandhub.theme.useAppColors
import org.hallen.commandhub.terminal.AnsiColorMapper

/**
 * Input para terminal interactiva con soporte para Ctrl+C y envío de comandos
 */
@Composable
fun InteractiveTerminalInput(
    onSendCommand: (String) -> Unit,
    onSendInterrupt: () -> Unit,
    isProcessRunning: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    var inputText by remember { mutableStateOf("") }
    var showCtrlCHint by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AnsiColorMapper.getTerminalBackground(),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prompt visual
            Text(
                text = ">",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = colors.primary
            )
            
            // TextField para entrada de comandos
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown) {
                            when {
                                // Ctrl+C - Enviar señal de interrupción
                                keyEvent.isCtrlPressed && keyEvent.key == Key.C -> {
                                    onSendInterrupt()
                                    showCtrlCHint = true
                                    true
                                }
                                // Enter - Enviar comando
                                keyEvent.key == Key.Enter && !keyEvent.isShiftPressed -> {
                                    if (inputText.isNotBlank()) {
                                        onSendCommand(inputText)
                                        inputText = ""
                                    }
                                    true
                                }
                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                placeholder = {
                    Text(
                        text = "Escribe un comando...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AnsiColorMapper.getTerminalBackground(),
                    unfocusedContainerColor = AnsiColorMapper.getTerminalBackground(),
                    focusedTextColor = AnsiColorMapper.getDefaultTextColor(),
                    unfocusedTextColor = AnsiColorMapper.getDefaultTextColor(),
                    cursorColor = colors.primary,
                    focusedIndicatorColor = colors.primary.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = colors.textSecondary.copy(alpha = 0.3f)
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendCommand(inputText)
                            inputText = ""
                        }
                    }
                )
            )
            
            // Botón de interrupción
            if (isProcessRunning) {
                IconButton(
                    onClick = {
                        onSendInterrupt()
                        showCtrlCHint = true
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Interrumpir",
                        tint = colors.danger,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Botón enviar
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendCommand(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Enviar comando",
                    tint = if (inputText.isNotBlank()) colors.primary else colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    // Snackbar temporal para mostrar que se envió Ctrl+C
    if (showCtrlCHint) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            showCtrlCHint = false
        }
    }
}
