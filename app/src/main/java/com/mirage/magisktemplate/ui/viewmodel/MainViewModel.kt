package com.mirage.magisktemplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirage.magisktemplate.core.model.ModuleConfig
import com.mirage.magisktemplate.core.model.ModuleInfo
import com.mirage.magisktemplate.core.model.ModuleSettings
import com.mirage.magisktemplate.core.model.RootInfo
import com.mirage.magisktemplate.core.root.ModuleRepository
import com.mirage.magisktemplate.core.root.ShellManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val rootInfo: RootInfo = RootInfo(),
    val moduleInfo: ModuleInfo = ModuleInfo(),
    val settings: ModuleSettings = ModuleSettings(),
    val isSavingSettings: Boolean = false,
    val terminalOutput: String = "Ready for execution.\nSelect a diagnostic or run action.sh below.",
    val terminalLastCode: Int? = null,
    val isRunningCommand: Boolean = false,
    val serviceLogs: String = "",
    val isLoadingLogs: Boolean = false,
    val userMessage: String? = null
)

class MainViewModel(
    private val repository: ModuleRepository = ModuleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val root = repository.getRootInfo()
            val module = repository.getModuleInfo()
            val settings = repository.loadSettings()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    rootInfo = root,
                    moduleInfo = module,
                    settings = settings
                )
            }
        }
    }

    fun toggleModule(enable: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.toggleModule(enable)
            val updatedModule = repository.getModuleInfo()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    moduleInfo = updatedModule,
                    userMessage = if (enable) "Module enabled (Effective upon reboot)" else "Module disabled (Safe mode)"
                )
            }
        }
    }

    fun updateSettings(transform: (ModuleSettings) -> ModuleSettings) {
        _uiState.update { it.copy(settings = transform(it.settings)) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingSettings = true) }
            val res = repository.saveSettings(_uiState.value.settings)
            _uiState.update {
                it.copy(
                    isSavingSettings = false,
                    userMessage = if (res.isSuccess) "Configuration written to ${ModuleConfig.CONFIG_FILE}" else "Failed to save configuration: ${res.errorText}"
                )
            }
        }
    }

    fun runModuleAction() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunningCommand = true,
                    terminalOutput = "Executing ${ModuleConfig.ACTION_SCRIPT}...\n"
                )
            }
            val res = repository.runModuleAction()
            val output = buildString {
                if (res.stdout.isNotEmpty()) appendLine(res.stdout.joinToString("\n"))
                if (res.stderr.isNotEmpty()) appendLine("STDERR:\n" + res.stderr.joinToString("\n"))
                appendLine("\n[Exit Code: ${res.exitCode}]")
            }
            _uiState.update {
                it.copy(
                    isRunningCommand = false,
                    terminalOutput = output,
                    terminalLastCode = res.exitCode
                )
            }
        }
    }

    fun executeDiagnostic(commandName: String, command: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunningCommand = true,
                    terminalOutput = "Running $commandName ($command)...\n"
                )
            }
            val res = ShellManager.exec(command)
            val output = buildString {
                if (res.stdout.isNotEmpty()) appendLine(res.stdout.joinToString("\n"))
                if (res.stderr.isNotEmpty()) appendLine("STDERR:\n" + res.stderr.joinToString("\n"))
                appendLine("\n[Exit Code: ${res.exitCode}]")
            }
            _uiState.update {
                it.copy(
                    isRunningCommand = false,
                    terminalOutput = output,
                    terminalLastCode = res.exitCode
                )
            }
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLogs = true) }
            val logs = repository.readServiceLog(250)
            _uiState.update {
                it.copy(
                    isLoadingLogs = false,
                    serviceLogs = logs
                )
            }
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearServiceLog()
            _uiState.update {
                it.copy(
                    serviceLogs = "Logs cleared.",
                    userMessage = "Service log file reset."
                )
            }
        }
    }

    fun restartSystemUI() {
        viewModelScope.launch {
            val res = ShellManager.restartSystemUI()
            _uiState.update {
                it.copy(userMessage = if (res.isSuccess) "SystemUI restarted" else "Error restarting SystemUI")
            }
        }
    }

    fun softReboot() {
        viewModelScope.launch {
            ShellManager.softReboot()
        }
    }

    fun rebootSystem() {
        viewModelScope.launch {
            ShellManager.rebootSystem()
        }
    }

    fun rebootRecovery() {
        viewModelScope.launch {
            ShellManager.rebootRecovery()
        }
    }

    fun rebootBootloader() {
        viewModelScope.launch {
            ShellManager.rebootBootloader()
        }
    }

    fun dropCaches() {
        viewModelScope.launch {
            val res = ShellManager.dropCaches()
            _uiState.update {
                it.copy(userMessage = if (res.isSuccess) "Caches cleared & sync completed" else "Failed to drop caches")
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
