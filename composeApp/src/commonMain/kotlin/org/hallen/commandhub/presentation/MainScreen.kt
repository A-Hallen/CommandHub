package org.hallen.commandhub.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import org.hallen.commandhub.presentation.components.CommandToolbar
import org.hallen.commandhub.presentation.components.CommandDialog
import org.hallen.commandhub.presentation.components.ProjectDialog
import org.hallen.commandhub.presentation.components.CategoryDialog
import org.hallen.commandhub.presentation.components.ModernExecutionPanel
import org.hallen.commandhub.presentation.components.Sidebar
import org.hallen.commandhub.presentation.components.ModernSidebar
import org.hallen.commandhub.presentation.components.SidebarNav
import org.hallen.commandhub.presentation.components.CommandsGridView
import org.hallen.commandhub.presentation.components.DashboardView
import org.hallen.commandhub.presentation.components.ProjectsView
import org.hallen.commandhub.presentation.components.AllCommandsView
import org.hallen.commandhub.presentation.components.SettingsView

/**
 * Pantalla principal de la aplicación
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.onPreviewKeyEvent { keyEvent ->
            // Manejar atajos de teclado globales
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    // Ctrl+N: Nuevo comando
                    keyEvent.isCtrlPressed && keyEvent.key == Key.N -> {
                        viewModel.onShowCommandDialog()
                        true
                    }
                    // Ctrl+F: Enfocar búsqueda (por ahora solo muestra el diálogo)
                    keyEvent.isCtrlPressed && keyEvent.key == Key.F -> {
                        // TODO: Enfocar campo de búsqueda
                        true
                    }
                    // Ctrl+E: Enfocar ejecución rápida
                    keyEvent.isCtrlPressed && keyEvent.key == Key.E -> {
                        // TODO: Enfocar campo de ejecución rápida
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        },
        topBar = {
            val (currentSectionTitle, currentSectionSubtitle) = when (uiState.currentNav) {
                SidebarNav.DASHBOARD -> Pair("Dashboard", "Comandos recientes y ejecución rápida")
                SidebarNav.COMMANDS -> Pair("Biblioteca de Comandos", "Todos tus comandos guardados")
                SidebarNav.FAVORITES -> Pair("Favoritos", "Tus comandos marcados como favoritos")
                SidebarNav.PROJECTS -> Pair("Proyectos", "Organiza comandos por proyecto")
                SidebarNav.TERMINAL -> Pair("Terminal", "Visualiza las ejecuciones de comandos")
                SidebarNav.SETTINGS -> Pair("Configuración", "Personaliza tu experiencia")
            }
            
            CommandToolbar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                currentSection = currentSectionTitle,
                currentSectionSubtitle = currentSectionSubtitle
            )
        }
    ) { paddingValues ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Modern Sidebar minimalista (80px)
            ModernSidebar(
                selectedNav = uiState.currentNav,
                onNavSelected = viewModel::onNavigationChange,
                onNewCommand = { viewModel.onShowCommandDialog() },
                modifier = Modifier.fillMaxHeight()
            )

            // Área principal dividida entre comandos y ejecuciones
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                // Lista de comandos (arriba) - ocupa todo el espacio disponible
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                        uiState.error != null -> {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {
                            // Mostrar contenido según navegación
                            when (uiState.currentNav) {
                                SidebarNav.TERMINAL -> {
                                    // Vista completa de terminal (no colapsible)
                                    ModernExecutionPanel(
                                        executions = uiState.executions,
                                        selectedExecutionId = uiState.selectedExecutionId,
                                        onExecutionSelected = { id -> 
                                            viewModel.onExecutionSelected(id)
                                        },
                                        onCloseExecution = { id ->
                                            viewModel.onCloseExecution(id)
                                        },
                                        onStopExecution = { id ->
                                            viewModel.onStopExecution(id)
                                        },
                                        onSendCommand = { executionId, command ->
                                            viewModel.sendCommandToInteractiveTerminal(executionId, command)
                                        },
                                        onSendInterrupt = { executionId ->
                                            viewModel.sendInterruptSignal(executionId)
                                        },
                                        onNewInteractiveTerminal = {
                                            viewModel.startInteractiveTerminal()
                                        },
                                        isCollapsible = false,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                SidebarNav.DASHBOARD -> {
                                    // Dashboard con estadísticas y comandos recientes
                                    DashboardView(
                                        commands = uiState.commands,
                                        categories = uiState.categories,
                                        selectedCommand = uiState.selectedCommand,
                                        onCommandSelected = viewModel::onCommandSelected,
                                        onCommandExecute = { command ->
                                            viewModel.executeCommand(command)
                                        },
                                        onCommandEdit = { command ->
                                            viewModel.onShowCommandDialog(command)
                                        },
                                        onCommandDelete = { command ->
                                            viewModel.deleteCommand(command)
                                        },
                                        onToggleFavorite = { command ->
                                            viewModel.toggleFavorite(command)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                SidebarNav.FAVORITES -> {
                                    // Mostrar solo favoritos
                                    CommandsGridView(
                                        commands = uiState.commands.filter { it.isFavorite },
                                        categories = uiState.categories,
                                        selectedCommand = uiState.selectedCommand,
                                        onCommandSelected = viewModel::onCommandSelected,
                                        onCommandExecute = { command ->
                                            viewModel.executeCommand(command)
                                        },
                                        onCommandEdit = { command ->
                                            viewModel.onShowCommandDialog(command)
                                        },
                                        onCommandDelete = { command ->
                                            viewModel.deleteCommand(command)
                                        },
                                        onToggleFavorite = { command ->
                                            viewModel.toggleFavorite(command)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                SidebarNav.PROJECTS -> {
                                    // Vista de proyectos
                                    ProjectsView(
                                        commands = uiState.commands,
                                        projects = uiState.projects,
                                        categories = uiState.categories,
                                        selectedCommand = uiState.selectedCommand,
                                        onCommandSelected = viewModel::onCommandSelected,
                                        onCommandExecute = { command ->
                                            viewModel.executeCommand(command)
                                        },
                                        onCommandEdit = { command ->
                                            viewModel.onShowCommandDialog(command)
                                        },
                                        onCommandDelete = { command ->
                                            viewModel.deleteCommand(command)
                                        },
                                        onToggleFavorite = { command ->
                                            viewModel.toggleFavorite(command)
                                        },
                                        onProjectAdd = {
                                            viewModel.onCreateProject()
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                SidebarNav.SETTINGS -> {
                                    // Vista de configuración
                                    SettingsView(
                                        categories = uiState.categories,
                                        onCategoryAdd = { viewModel.onCreateCategory() },
                                        onCategoryEdit = { category -> viewModel.onEditCategory(category) },
                                        onCategoryDelete = { categoryId -> viewModel.deleteCategory(categoryId) },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                else -> {
                                    // Vista de TODOS los comandos (Biblioteca completa)
                                    AllCommandsView(
                                        commands = uiState.commands,
                                        categories = uiState.categories,
                                        selectedCommand = uiState.selectedCommand,
                                        onCommandSelected = viewModel::onCommandSelected,
                                        onCommandExecute = { command ->
                                            viewModel.executeCommand(command)
                                        },
                                        onCommandEdit = { command ->
                                            viewModel.onShowCommandDialog(command)
                                        },
                                        onCommandDelete = { command ->
                                            viewModel.deleteCommand(command)
                                        },
                                        onToggleFavorite = { command ->
                                            viewModel.toggleFavorite(command)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Panel de ejecuciones (abajo) - Solo mostrar si NO estamos en la vista de terminal
                if (uiState.currentNav != SidebarNav.TERMINAL) {
                    // Divider horizontal
                    HorizontalDivider()

                    // Panel de ejecuciones (abajo) - Usa weight cuando expandido, sin weight cuando contraído
                    ModernExecutionPanel(
                        executions = uiState.executions,
                        selectedExecutionId = uiState.selectedExecutionId,
                        onExecutionSelected = { id -> 
                            viewModel.onExecutionSelected(id)
                        },
                        onCloseExecution = { id ->
                            viewModel.onCloseExecution(id)
                        },
                        onStopExecution = { id ->
                            viewModel.onStopExecution(id)
                        },
                        onSendCommand = { executionId, command ->
                            viewModel.sendCommandToInteractiveTerminal(executionId, command)
                        },
                        onSendInterrupt = { executionId ->
                            viewModel.sendInterruptSignal(executionId)
                        },
                        isCollapsible = true,
                        initialExpanded = uiState.isTerminalExpanded,
                        onExpandedChange = { isExpanded ->
                            viewModel.onTerminalExpandedChange(isExpanded)
                        },
                        modifier = if (uiState.isTerminalExpanded) {
                            Modifier.weight(1f).fillMaxWidth()
                        } else {
                            Modifier.fillMaxWidth() // Sin weight cuando está contraído
                        }
                    )
                }
            }
        }
    }

    // Diálogo para crear/editar comandos
    if (uiState.showCommandDialog) {
        CommandDialog(
            command = uiState.editingCommand,
            projects = uiState.projects,
            categories = uiState.categories,
            onDismiss = viewModel::onDismissCommandDialog,
            onSave = viewModel::saveCommand
        )
    }
    
    // Diálogo para crear/editar proyectos
    if (uiState.showProjectDialog) {
        ProjectDialog(
            project = uiState.editingProject,
            onDismiss = viewModel::onDismissProjectDialog,
            onSave = viewModel::saveProject
        )
    }
    
    // Diálogo para crear/editar categorías
    if (uiState.showCategoryDialog) {
        CategoryDialog(
            category = uiState.editingCategory,
            onDismiss = viewModel::onDismissCategoryDialog,
            onSave = viewModel::saveCategory
        )
    }
}
