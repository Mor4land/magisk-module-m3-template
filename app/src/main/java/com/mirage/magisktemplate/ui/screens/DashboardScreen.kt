package com.mirage.magisktemplate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mirage.magisktemplate.core.model.ModuleConfig
import com.mirage.magisktemplate.ui.components.PreferenceCategoryHeader
import com.mirage.magisktemplate.ui.components.StatusHeroCard
import com.mirage.magisktemplate.ui.viewmodel.MainUiState

@Composable
fun DashboardScreen(
    state: MainUiState,
    onToggleModule: (Boolean) -> Unit,
    onRestartSystemUI: () -> Unit,
    onRunAction: () -> Unit,
    onOpenPowerMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Module Status Card
        StatusHeroCard(
            rootInfo = state.rootInfo,
            moduleInfo = state.moduleInfo,
            onToggleModule = onToggleModule
        )

        // Quick Actions Row
        PreferenceCategoryHeader(title = "QUICK ACTIONS")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onRestartSystemUI,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("UI Restart")
            }

            Button(
                onClick = onRunAction,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                enabled = state.rootInfo.isRooted && state.moduleInfo.isInstalled
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("action.sh")
            }

            OutlinedButton(
                onClick = onOpenPowerMenu,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Power")
            }
        }

        // Module Metadata Card
        PreferenceCategoryHeader(title = "MODULE SPECIFICATIONS")
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetaItem(icon = Icons.Default.Info, label = "Module ID", value = state.moduleInfo.id.ifEmpty { ModuleConfig.MODULE_ID })
                MetaItem(icon = Icons.Default.Person, label = "Author", value = state.moduleInfo.author.ifEmpty { ModuleConfig.MODULE_AUTHOR })
                MetaItem(icon = Icons.Default.Speed, label = "Version", value = "${state.moduleInfo.version} (${state.moduleInfo.versionCode})")
                MetaItem(icon = Icons.Default.Folder, label = "Path", value = ModuleConfig.MODULE_DIR)
                MetaItem(icon = Icons.Default.Description, label = "Description", value = state.moduleInfo.description)
            }
        }

        // Environment Details
        PreferenceCategoryHeader(title = "ROOT ENVIRONMENT")
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetaItem(
                    icon = Icons.Default.Info,
                    label = "Root Manager",
                    value = "${state.rootInfo.manager.displayName} (${state.rootInfo.managerVersion})"
                )
                MetaItem(
                    icon = Icons.Default.Info,
                    label = "SELinux Status",
                    value = state.rootInfo.seLinuxMode
                )
                MetaItem(
                    icon = Icons.Default.Folder,
                    label = "SU Binary",
                    value = state.rootInfo.suBinaryPath
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetaItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
