package com.mirage.magisktemplate.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mirage.magisktemplate.core.model.ModuleInfo
import com.mirage.magisktemplate.core.model.RootInfo
import com.mirage.magisktemplate.core.model.RootManager
import com.mirage.magisktemplate.ui.theme.StatusError
import com.mirage.magisktemplate.ui.theme.StatusErrorContainer
import com.mirage.magisktemplate.ui.theme.StatusSuccess
import com.mirage.magisktemplate.ui.theme.StatusSuccessContainer
import com.mirage.magisktemplate.ui.theme.StatusWarning
import com.mirage.magisktemplate.ui.theme.StatusWarningContainer

@Composable
fun StatusHeroCard(
    rootInfo: RootInfo,
    moduleInfo: ModuleInfo,
    onToggleModule: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusText, statusBg, statusFg, statusIcon) = when {
        !rootInfo.isRooted -> Quadruple(
            "No Root Access",
            StatusErrorContainer,
            StatusError,
            Icons.Default.Error
        )
        !moduleInfo.isInstalled -> Quadruple(
            "Module Not Installed",
            StatusWarningContainer,
            StatusWarning,
            Icons.Default.Warning
        )
        moduleInfo.isRemovePending -> Quadruple(
            "Remove on Reboot",
            StatusErrorContainer,
            StatusError,
            Icons.Default.PowerSettingsNew
        )
        moduleInfo.isUpdatePending -> Quadruple(
            "Update Pending",
            StatusWarningContainer,
            StatusWarning,
            Icons.Default.Warning
        )
        moduleInfo.isEnabled -> Quadruple(
            "Active & Enabled",
            StatusSuccessContainer,
            StatusSuccess,
            Icons.Default.CheckCircle
        )
        else -> Quadruple(
            "Disabled (Safe Mode)",
            StatusWarningContainer,
            StatusWarning,
            Icons.Default.Warning
        )
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Icon + Module Name + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = moduleInfo.name.ifEmpty { "Magisk Module" },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${moduleInfo.version} (${moduleInfo.id})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (rootInfo.isRooted && moduleInfo.isInstalled) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = moduleInfo.isEnabled,
                        onCheckedChange = onToggleModule
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusFg,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = statusFg
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoTag(
                    icon = Icons.Default.Security,
                    label = if (rootInfo.isRooted) "${rootInfo.manager.displayName} ${rootInfo.managerVersion}" else "Unrooted",
                    isSuccess = rootInfo.isRooted
                )
                InfoTag(
                    icon = Icons.Default.Security,
                    label = "SELinux: ${rootInfo.seLinuxMode}",
                    isSuccess = rootInfo.seLinuxMode.equals("Enforcing", ignoreCase = true)
                )
            }
        }
    }
}

@Composable
private fun InfoTag(
    icon: ImageVector,
    label: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) StatusSuccess else StatusError)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
