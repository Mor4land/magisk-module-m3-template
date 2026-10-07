package com.mirage.magisktemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.mirage.magisktemplate.core.model.ModuleConfig
import com.mirage.magisktemplate.ui.components.SystemRebootSheet
import com.mirage.magisktemplate.ui.screens.AboutScreen
import com.mirage.magisktemplate.ui.screens.DashboardScreen
import com.mirage.magisktemplate.ui.screens.LogsScreen
import com.mirage.magisktemplate.ui.screens.SettingsScreen
import com.mirage.magisktemplate.ui.screens.TerminalScreen
import com.mirage.magisktemplate.ui.theme.MagiskTemplateTheme
import com.mirage.magisktemplate.ui.viewmodel.MainViewModel

sealed class NavDestination(
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : NavDestination("Dashboard", Icons.Default.Dashboard)
    data object Tweaks : NavDestination("Tweaks", Icons.Default.Tune)
    data object Terminal : NavDestination("Terminal", Icons.Default.Terminal)
    data object Logs : NavDestination("Logs", Icons.AutoMirrored.Filled.Article)
    data object About : NavDestination("About", Icons.Default.Info)

    companion object {
        val items = listOf(Dashboard, Tweaks, Terminal, Logs, About)
    }
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MagiskTemplateTheme {
                val state by viewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                var selectedTab by remember { mutableIntStateOf(0) }
                var showPowerMenu by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                LaunchedEffect(state.userMessage) {
                    val msg = state.userMessage
                    if (msg != null) {
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearUserMessage()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = NavDestination.items[selectedTab].title,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            actions = {
                                IconButton(onClick = { viewModel.refreshAll() }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavDestination.items.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.title
                                        )
                                    },
                                    label = { Text(destination.title) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                state = state,
                                onToggleModule = { viewModel.toggleModule(it) },
                                onRestartSystemUI = { viewModel.restartSystemUI() },
                                onRunAction = {
                                    viewModel.runModuleAction()
                                    selectedTab = 2 // jump to terminal tab
                                },
                                onOpenPowerMenu = { showPowerMenu = true }
                            )
                            1 -> SettingsScreen(
                                state = state,
                                onUpdateSettings = { viewModel.updateSettings(it) },
                                onSaveSettings = { viewModel.saveSettings() }
                            )
                            2 -> TerminalScreen(
                                state = state,
                                onRunAction = { viewModel.runModuleAction() },
                                onRunDiagnostic = { name, cmd -> viewModel.executeDiagnostic(name, cmd) }
                            )
                            3 -> LogsScreen(
                                state = state,
                                onRefreshLogs = { viewModel.loadLogs() },
                                onClearLogs = { viewModel.clearLogs() }
                            )
                            4 -> AboutScreen(
                                state = state
                            )
                        }
                    }

                    if (showPowerMenu) {
                        SystemRebootSheet(
                            sheetState = sheetState,
                            onDismissRequest = { showPowerMenu = false },
                            onRestartSystemUI = { viewModel.restartSystemUI() },
                            onSoftReboot = { viewModel.softReboot() },
                            onRebootSystem = { viewModel.rebootSystem() },
                            onRebootRecovery = { viewModel.rebootRecovery() },
                            onRebootBootloader = { viewModel.rebootBootloader() },
                            onDropCaches = { viewModel.dropCaches() }
                        )
                    }
                }
            }
        }
    }
}
