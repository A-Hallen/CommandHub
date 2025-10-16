package org.hallen.commandhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.domain.model.*
import org.hallen.commandhub.terminal.TerminalSettingsManager
import org.hallen.commandhub.theme.useAppColors

/**
 * Sección completa de configuración del terminal
 */
@Composable
fun TerminalSettingsSection(
    settingsManager: TerminalSettingsManager,
    modifier: Modifier = Modifier
) {
    val colors = useAppColors()
    val settings = settingsManager.settings.value
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // === APARIENCIA ===
        SettingsSectionCard(title = "Apariencia del Terminal") {
            // Font Size
            SliderSetting(
                icon = Icons.Filled.FormatSize,
                title = "Tamaño de Fuente",
                description = "Ajusta el tamaño del texto",
                value = settings.fontSize.toFloat(),
                valueRange = 8f..24f,
                steps = 15,
                valueLabel = "${settings.fontSize}sp",
                onValueChange = { settingsManager.updateFontSize(it.toInt()) }
            )
            
            Divider(color = colors.border)
            
            // Line Height
            SliderSetting(
                icon = Icons.Filled.LineWeight,
                title = "Altura de Línea",
                description = "Espacio entre líneas",
                value = settings.lineHeight.toFloat(),
                valueRange = 12f..32f,
                steps = 19,
                valueLabel = "${settings.lineHeight}sp",
                onValueChange = { settingsManager.updateLineHeight(it.toInt()) }
            )
            
            Divider(color = colors.border)
            
            // Color Scheme
            DropdownSetting(
                icon = Icons.Filled.Palette,
                title = "Esquema de Colores",
                description = "Tema de colores ANSI",
                selectedValue = settings.colorScheme,
                options = listOf(
                    AnsiColorScheme.DEFAULT,
                    AnsiColorScheme.DRACULA,
                    AnsiColorScheme.NORD,
                    AnsiColorScheme.MONOKAI
                ),
                optionLabel = { 
                    when(it) {
                        AnsiColorScheme.DEFAULT -> "Por Defecto"
                        AnsiColorScheme.DRACULA -> "Dracula"
                        AnsiColorScheme.NORD -> "Nord"
                        AnsiColorScheme.MONOKAI -> "Monokai"
                        else -> it.name
                    }
                },
                onValueSelected = { settingsManager.updateColorScheme(it) }
            )
        }
        
        // === COMPORTAMIENTO ===
        SettingsSectionCard(title = "Comportamiento") {
            ToggleSetting(
                icon = Icons.Filled.ArrowDownward,
                title = "Auto-scroll",
                description = "Desplazarse automáticamente al final del output",
                checked = settings.autoScroll,
                onCheckedChange = { settingsManager.updateAutoScroll(it) }
            )
            
            Divider(color = colors.border)
            
            ToggleSetting(
                icon = Icons.Filled.Animation,
                title = "Scroll Suave",
                description = "Animación suave al desplazarse",
                checked = settings.smoothScroll,
                onCheckedChange = { settingsManager.updateSmoothScroll(it) }
            )
            
            Divider(color = colors.border)
            
            ToggleSetting(
                icon = Icons.Filled.WrapText,
                title = "Ajustar Líneas",
                description = "Ajustar líneas largas al ancho del terminal",
                checked = settings.wordWrap,
                onCheckedChange = { settingsManager.updateWordWrap(it) }
            )
        }
        
        // === DISPLAY ===
        SettingsSectionCard(title = "Visualización") {
            ToggleSetting(
                icon = Icons.Filled.Schedule,
                title = "Mostrar Timestamps",
                description = "Muestra la hora al inicio de cada línea",
                checked = settings.showTimestamps,
                onCheckedChange = { settingsManager.updateShowTimestamps(it) }
            )
            
            if (settings.showTimestamps) {
                Divider(color = colors.border)
                
                DropdownSetting(
                    icon = Icons.Filled.AccessTime,
                    title = "Formato de Hora",
                    description = "Cómo mostrar el timestamp",
                    selectedValue = settings.timestampFormat,
                    options = listOf(
                        TimestampFormat.TIME_ONLY,
                        TimestampFormat.TIME_MS,
                        TimestampFormat.DATE_TIME
                    ),
                    optionLabel = { 
                        when(it) {
                            TimestampFormat.TIME_ONLY -> "HH:mm:ss"
                            TimestampFormat.TIME_MS -> "HH:mm:ss.SSS"
                            TimestampFormat.DATE_TIME -> "yyyy-MM-dd HH:mm:ss"
                            else -> it.name
                        }
                    },
                    onValueSelected = { settingsManager.updateTimestampFormat(it) }
                )
            }
        }
        
        // === AVANZADO ===
        SettingsSectionCard(title = "Avanzado") {
            ToggleSetting(
                icon = Icons.Filled.Code,
                title = "Parsear Códigos ANSI",
                description = "Procesar y mostrar colores ANSI",
                checked = settings.parseAnsiCodes,
                onCheckedChange = { settingsManager.updateParseAnsiCodes(it) }
            )
        }
        
        // === PRESETS ===
        SettingsSectionCard(title = "Presets Rápidos") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        text = "Por Defecto",
                        modifier = Modifier.weight(1f),
                        onClick = { settingsManager.applyPreset(TerminalDefaults.DEFAULT) }
                    )
                    PresetButton(
                        text = "Compacto",
                        modifier = Modifier.weight(1f),
                        onClick = { settingsManager.applyPreset(TerminalDefaults.COMPACT) }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        text = "Grande",
                        modifier = Modifier.weight(1f),
                        onClick = { settingsManager.applyPreset(TerminalDefaults.LARGE) }
                    )
                    PresetButton(
                        text = "Developer",
                        modifier = Modifier.weight(1f),
                        onClick = { settingsManager.applyPreset(TerminalDefaults.DEVELOPER) }
                    )
                }
                
                OutlinedButton(
                    onClick = { settingsManager.resetToDefaults() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Restablecer a Valores por Defecto")
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
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
private fun ToggleSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SliderSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    onValueChange: (Float) -> Unit
) {
    val colors = useAppColors()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
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
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = colors.primary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary.copy(alpha = 0.2f))
            ) {
                Text(
                    text = valueLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun <T> DropdownSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    selectedValue: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onValueSelected: (T) -> Unit
) {
    val colors = useAppColors()
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
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
        
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = optionLabel(selectedValue),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}
