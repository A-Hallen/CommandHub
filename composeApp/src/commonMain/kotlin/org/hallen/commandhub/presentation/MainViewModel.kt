package org.hallen.commandhub.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.hallen.commandhub.domain.model.Command
import org.hallen.commandhub.domain.model.Project
import org.hallen.commandhub.domain.model.Category
import org.hallen.commandhub.domain.repository.CommandRepository
import org.hallen.commandhub.domain.repository.ProjectRepository
import org.hallen.commandhub.domain.repository.CategoryRepository
import org.hallen.commandhub.domain.model.ExecutionStatus
import org.hallen.commandhub.execution.CommandExecutorInterface
import org.hallen.commandhub.execution.ExecutionOutput

/**
 * ViewModel principal de la aplicación
 */
class MainViewModel(
    private val commandRepository: CommandRepository,
    private val projectRepository: ProjectRepository,
    private val categoryRepository: CategoryRepository,
    private val commandExecutor: CommandExecutorInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
        loadProjects()
        loadCategories()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // Establecer loading en false después de un corto delay para asegurar que la UI se actualice
                kotlinx.coroutines.delay(100)
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                commandRepository.getAllCommands().collect { allCommands ->
                    allCommandsCache = allCommands
                    applyFilters()
                }
            } catch (e: Exception) {
                println("Error loading commands: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            try {
                projectRepository.getAllProjects().collect { projects ->
                    _uiState.value = _uiState.value.copy(projects = projects)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }

        viewModelScope.launch {
            try {
                categoryRepository.getAllCategories().collect { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }
    
    fun onQuickCommandChange(command: String) {
        _uiState.value = _uiState.value.copy(quickCommand = command)
    }
    
    fun onQuickExecute() {
        val quickCmd = _uiState.value.quickCommand.trim()
        if (quickCmd.isNotBlank()) {
            // Crear un comando temporal para ejecutar
            val tempCommand = Command(
                id = 0L, // ID temporal
                name = "Ejecución Rápida",
                command = quickCmd,
                description = "",
                workingDirectory = System.getProperty("user.home") ?: "",
                shell = org.hallen.commandhub.domain.model.ShellType.POWERSHELL,
                projectId = null,
                categoryId = null,
                isFavorite = false,
                tags = emptyList()
            )
            executeCommand(tempCommand)
            // Limpiar el campo
            _uiState.value = _uiState.value.copy(quickCommand = "")
        }
    }
    
    fun onNavigationChange(nav: org.hallen.commandhub.presentation.components.SidebarNav) {
        _uiState.value = _uiState.value.copy(currentNav = nav)
    }
    
    
    fun onTerminalExpandedChange(isExpanded: Boolean) {
        _uiState.value = _uiState.value.copy(isTerminalExpanded = isExpanded)
    }

    private var allCommandsCache: List<Command> = emptyList()
    
    private fun applyFilters() {
        val state = _uiState.value
        val filteredCommands = filterCommands(
            commands = allCommandsCache,
            searchQuery = state.searchQuery,
            projectId = state.selectedProjectId,
            categoryId = state.selectedCategoryId,
            showFavoritesOnly = state.showFavoritesOnly
        )
        _uiState.value = state.copy(commands = filteredCommands)
    }

    private fun filterCommands(
        commands: List<Command>,
        searchQuery: String,
        projectId: Long?,
        categoryId: Long?,
        showFavoritesOnly: Boolean
    ): List<Command> {
        return commands.filter { command ->
            // Filtro de búsqueda
            val matchesSearch = searchQuery.isBlank() ||
                    command.name.contains(searchQuery, ignoreCase = true) ||
                    command.description.contains(searchQuery, ignoreCase = true) ||
                    command.command.contains(searchQuery, ignoreCase = true) ||
                    command.tags.any { it.contains(searchQuery, ignoreCase = true) }

            // Filtro de proyecto
            val matchesProject = projectId == null || command.projectId == projectId

            // Filtro de categoría
            val matchesCategory = categoryId == null || command.categoryId == categoryId

            // Filtro de favoritos
            val matchesFavorite = !showFavoritesOnly || command.isFavorite

            matchesSearch && matchesProject && matchesCategory && matchesFavorite
        }
    }

    fun onCommandSelected(command: Command?) {
        _uiState.value = _uiState.value.copy(selectedCommand = command)
    }

    fun onProjectFilterChange(projectId: Long?) {
        _uiState.value = _uiState.value.copy(selectedProjectId = projectId)
        applyFilters()
    }

    fun onCategoryFilterChange(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        applyFilters()
    }

    fun onShowFavoritesChange(showFavorites: Boolean) {
        _uiState.value = _uiState.value.copy(showFavoritesOnly = showFavorites)
        applyFilters()
    }

    fun onShowCommandDialog(command: Command? = null) {
        _uiState.value = _uiState.value.copy(
            showCommandDialog = true,
            editingCommand = command
        )
    }

    fun onDismissCommandDialog() {
        _uiState.value = _uiState.value.copy(
            showCommandDialog = false,
            editingCommand = null
        )
    }

    fun saveCommand(command: Command) {
        viewModelScope.launch {
            try {
                println("[MainViewModel] saveCommand() - Comando: ${command.name}, ID: ${command.id}")
                if (command.id == 0L) {
                    println("[MainViewModel] Insertando nuevo comando...")
                    val newId = commandRepository.insertCommand(command)
                    println("[MainViewModel] Comando insertado con ID: $newId")
                } else {
                    println("[MainViewModel] Actualizando comando existente...")
                    commandRepository.updateCommand(command)
                    println("[MainViewModel] Comando actualizado")
                }
                // No es necesario actualizar manualmente, el flow lo hará automáticamente
            } catch (e: Exception) {
                println("[MainViewModel] ERROR en saveCommand(): ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCommand(command: Command) {
        viewModelScope.launch {
            try {
                println("[MainViewModel] deleteCommand() - ID: ${command.id}, Nombre: ${command.name}")
                commandRepository.deleteCommand(command.id)
                println("[MainViewModel] Comando eliminado")
                // No es necesario actualizar manualmente, el flow lo hará automáticamente
            } catch (e: Exception) {
                println("[MainViewModel] ERROR en deleteCommand(): ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun toggleFavorite(command: Command) {
        viewModelScope.launch {
            try {
                println("[MainViewModel] toggleFavorite() - ID: ${command.id}, Favorito actual: ${command.isFavorite}")
                commandRepository.updateCommand(
                    command.copy(
                        isFavorite = !command.isFavorite,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                println("[MainViewModel] Favorito actualizado a: ${!command.isFavorite}")
            } catch (e: Exception) {
                println("[MainViewModel] ERROR en toggleFavorite(): ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun executeCommand(command: Command) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val executionState = ExecutionState(
                command = command,
                status = ExecutionStatus.RUNNING,
                startTime = startTime
            )

            // Agregar la ejecución al estado y cambiar a vista Terminal
            val executions = _uiState.value.executions.toMutableMap()
            executions[executionState.id] = executionState
            _uiState.value = _uiState.value.copy(
                executions = executions,
                selectedExecutionId = executionState.id,
                currentNav = org.hallen.commandhub.presentation.components.SidebarNav.TERMINAL
            )

            // Ejecutar el comando y recolectar el output (pasar el executionId)
            try {
                commandExecutor.executeCommand(command, executionState.id).collect { output ->
                    val currentExecution = _uiState.value.executions[executionState.id]
                    if (currentExecution != null) {
                        val updatedExecution = when (output) {
                            is ExecutionOutput.Started -> currentExecution
                            is ExecutionOutput.StdOut -> currentExecution.copy(
                                output = currentExecution.output + output.line + "\n"
                            )
                            is ExecutionOutput.StdErr -> currentExecution.copy(
                                errorOutput = currentExecution.errorOutput + output.line + "\n"
                            )
                            is ExecutionOutput.Finished -> currentExecution.copy(
                                status = if (output.exitCode == 0) ExecutionStatus.COMPLETED else ExecutionStatus.FAILED,
                                exitCode = output.exitCode,
                                duration = output.duration,
                                endTime = output.endTime
                            )
                            is ExecutionOutput.Error -> currentExecution.copy(
                                status = ExecutionStatus.FAILED,
                                errorOutput = currentExecution.errorOutput + output.message + "\n"
                            )
                        }

                        val newExecutions = _uiState.value.executions.toMutableMap()
                        newExecutions[executionState.id] = updatedExecution
                        _uiState.value = _uiState.value.copy(executions = newExecutions)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun onExecutionSelected(executionId: String) {
        _uiState.value = _uiState.value.copy(selectedExecutionId = executionId)
    }

    fun onStopExecution(executionId: String) {
        // Detener el proceso
        commandExecutor.stopExecution(executionId)
        
        // Actualizar el estado a STOPPED
        val executions = _uiState.value.executions.toMutableMap()
        executions[executionId]?.let { execution ->
            executions[executionId] = execution.copy(
                status = ExecutionStatus.STOPPED,
                endTime = System.currentTimeMillis(),
                duration = System.currentTimeMillis() - execution.startTime
            )
        }
        _uiState.value = _uiState.value.copy(executions = executions)
    }
    
    fun onCloseExecution(executionId: String) {
        // Detener el proceso si aún está corriendo
        commandExecutor.stopExecution(executionId)
        
        val executions = _uiState.value.executions.toMutableMap()
        executions.remove(executionId)
        
        val newSelectedId = if (_uiState.value.selectedExecutionId == executionId) {
            executions.keys.firstOrNull()
        } else {
            _uiState.value.selectedExecutionId
        }
        
        _uiState.value = _uiState.value.copy(
            executions = executions,
            selectedExecutionId = newSelectedId
        )
    }
    
    /**
     * Inicia una nueva terminal interactiva vacía
     */
    fun startInteractiveTerminal(
        workingDirectory: String = System.getProperty("user.home") ?: "",
        shell: org.hallen.commandhub.domain.model.ShellType = org.hallen.commandhub.domain.model.ShellType.POWERSHELL
    ) {
        // Crear un comando vacío para una terminal nueva
        val interactiveCommand = Command(
            id = System.currentTimeMillis(), // ID único basado en timestamp
            name = "Terminal",
            command = "", // Sin comando inicial
            description = "Terminal interactiva",
            workingDirectory = workingDirectory,
            shell = shell,
            projectId = null,
            categoryId = null,
            isFavorite = false,
            tags = emptyList()
        )
        
        // Ejecutar como cualquier otro comando (ahora todos son interactivos)
        executeCommand(interactiveCommand)
    }
    
    /**
     * Envía un comando a una terminal interactiva activa
     */
    fun sendCommandToInteractiveTerminal(executionId: String, command: String) {
        println("[MainViewModel] Sending command to execution $executionId: $command")
        
        // Interceptar comandos especiales
        val trimmedCommand = command.trim()
        when {
            trimmedCommand == "cls" || trimmedCommand == "clear" -> {
                // Limpiar el output de la terminal
                val executions = _uiState.value.executions.toMutableMap()
                executions[executionId]?.let { execution ->
                    executions[executionId] = execution.copy(
                        output = "",
                        errorOutput = ""
                    )
                    _uiState.value = _uiState.value.copy(executions = executions)
                }
                // También enviar el comando al shell real para que actualice su estado
                commandExecutor.sendInputToTerminal(executionId, trimmedCommand)
            }
            else -> {
                // Comando normal - enviarlo al shell
                commandExecutor.sendInputToTerminal(executionId, command)
            }
        }
    }
    
    /**
     * Envía señal de interrupción (Ctrl+C) a una ejecución
     */
    fun sendInterruptSignal(executionId: String) {
        commandExecutor.sendInterruptSignal(executionId)
    }
    
    // Gestión de Proyectos
    fun onCreateProject() {
        _uiState.value = _uiState.value.copy(
            showProjectDialog = true,
            editingProject = null
        )
    }
    
    fun onEditProject(project: Project) {
        _uiState.value = _uiState.value.copy(
            showProjectDialog = true,
            editingProject = project
        )
    }
    
    fun onDismissProjectDialog() {
        _uiState.value = _uiState.value.copy(
            showProjectDialog = false,
            editingProject = null
        )
    }
    
    fun saveProject(project: Project) {
        viewModelScope.launch {
            try {
                if (project.id == 0L) {
                    projectRepository.insertProject(project)
                } else {
                    projectRepository.updateProject(project)
                }
                loadProjects()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(projectId)
                loadProjects()
                // Si el proyecto eliminado era el filtro activo, limpiar el filtro
                if (_uiState.value.selectedProjectId == projectId) {
                    onProjectFilterChange(null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // Gestión de Categorías
    fun onCreateCategory() {
        _uiState.value = _uiState.value.copy(
            showCategoryDialog = true,
            editingCategory = null
        )
    }
    
    fun onEditCategory(category: Category) {
        _uiState.value = _uiState.value.copy(
            showCategoryDialog = true,
            editingCategory = category
        )
    }
    
    fun onDismissCategoryDialog() {
        _uiState.value = _uiState.value.copy(
            showCategoryDialog = false,
            editingCategory = null
        )
    }
    
    fun saveCategory(category: Category) {
        viewModelScope.launch {
            try {
                if (category.id == 0L) {
                    categoryRepository.insertCategory(category)
                } else {
                    categoryRepository.updateCategory(category)
                }
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)
                loadCategories()
                // Si la categoría eliminada era el filtro activo, limpiar el filtro
                if (_uiState.value.selectedCategoryId == categoryId) {
                    onCategoryFilterChange(null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            try {
                projectRepository.getAllProjects().collect { projects ->
                    _uiState.value = _uiState.value.copy(projects = projects)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getAllCategories().collect { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

/**
 * Estado de la UI principal
 */
data class MainUiState(
    val commands: List<Command> = emptyList(),
    val projects: List<Project> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCommand: Command? = null,
    val selectedProjectId: Long? = null,
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val quickCommand: String = "",
    val showFavoritesOnly: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showCommandDialog: Boolean = false,
    val editingCommand: Command? = null,
    val showProjectDialog: Boolean = false,
    val editingProject: Project? = null,
    val showCategoryDialog: Boolean = false,
    val editingCategory: Category? = null,
    val executions: Map<String, ExecutionState> = emptyMap(),
    val selectedExecutionId: String? = null,
    val currentNav: org.hallen.commandhub.presentation.components.SidebarNav = org.hallen.commandhub.presentation.components.SidebarNav.COMMANDS,
    val isTerminalExpanded: Boolean = true
)
