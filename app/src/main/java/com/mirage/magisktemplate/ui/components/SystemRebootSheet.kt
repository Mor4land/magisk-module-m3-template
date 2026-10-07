package com.mirage.magisktemplate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemRebootSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onRestartSystemUI: () -> Unit,
    onSoftReboot: () -> Unit,
    onRebootSystem: () -> Unit,
    onRebootRecovery: () -> Unit,
    onRebootBootloader: () -> Unit,
    onDropCaches: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Quick System & Power Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Perform root-level system management operations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            PowerActionRow(
                title = "Restart SystemUI",
                subtitle = "Restart status bar, quick settings & navigation without reboot",
                icon = Icons.Default.Refresh,
                onClick = {
                    onRestartSystemUI()
                    onDismissRequest()
                }
            )

            PowerActionRow(
                title = "Soft Reboot (Zygote)",
                subtitle = "Restart Android runtime services without restarting kernel",
                icon = Icons.Default.RestartAlt,
                onClick = {
                    onSoftReboot()
                    onDismissRequest()
                }
            )

            PowerActionRow(
                title = "Reboot Device",
                subtitle = "Standard complete system reboot",
                icon = Icons.Default.PowerSettingsNew,
                onClick = {
                    onRebootSystem()
                    onDismissRequest()
                }
            )

            PowerActionRow(
                title = "Reboot to Recovery",
                subtitle = "Boot into TWRP, OrangeFox or Stock Recovery",
                icon = Icons.Default.ElectricalServices,
                onClick = {
                    onRebootRecovery()
                    onDismissRequest()
                }
            )

            PowerActionRow(
                title = "Reboot to Fastboot / Bootloader",
                subtitle = "Boot into device bootloader mode",
                icon = Icons.Default.PhoneAndroid,
                onClick = {
                    onRebootBootloader()
                    onDismissRequest()
                }
            )

            PowerActionRow(
                title = "Drop Page Caches & Sync",
                subtitle = "Flush filesystem buffers and clear memory caches",
                icon = Icons.Default.CleaningServices,
                onClick = {
                    onDropCaches()
                    onDismissRequest()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PowerActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
