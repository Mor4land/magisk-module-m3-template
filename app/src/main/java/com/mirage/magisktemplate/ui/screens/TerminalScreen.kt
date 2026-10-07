package com.mirage.magisktemplate.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.magisktemplate.core.model.ModuleConfig
import com.mirage.magisktemplate.ui.components.PreferenceCategoryHeader
import com.mirage.magisktemplate.ui.theme.StatusError
import com.mirage.magisktemplate.ui.theme.StatusSuccess
import com.mirage.magisktemplate.ui.viewmodel.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TerminalScreen(
    state: MainUiState,
    onRunAction: () -> Unit,
    onRunDiagnostic: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val terminalHScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Action Button
        Button(
            onClick = onRunAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.isRunningCommand && state.rootInfo.isRooted && state.moduleInfo.isInstalled
        ) {
            if (state.isRunningCommand) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Executing Script...")
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Execute Module action.sh")
            }
        }

        // Quick Diagnostic Commands
        PreferenceCategoryHeader(title = "PRESET ROOT DIAGNOSTICS")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { onRunDiagnostic("Memory Info", "free -m") },
                label = { Text("free -m") },
                enabled = !state.isRunningCommand && state.rootInfo.isRooted
            )
            AssistChip(
                onClick = { onRunDiagnostic("Disk Space", "df -h /data") },
                label = { Text("df -h /data") },
                enabled = !state.isRunningCommand && state.rootInfo.isRooted
            )
            AssistChip(
                onClick = { onRunDiagnostic("Installed Modules", "ls -l /data/adb/modules") },
                label = { Text("ls modules") },
                enabled = !state.isRunningCommand && state.rootInfo.isRooted
            )
            AssistChip(
                onClick = { onRunDiagnostic("Module Config", "cat \"${ModuleConfig.CONFIG_FILE}\"") },
                label = { Text("cat config.sh") },
                enabled = !state.isRunningCommand && state.rootInfo.isRooted
            )
            AssistChip(
                onClick = { onRunDiagnostic("Device Props", "getprop ro.product.model; getprop ro.build.version.release") },
                label = { Text("device model") },
                enabled = !state.isRunningCommand && state.rootInfo.isRooted
            )
        }

        // Terminal Console Output Box
        PreferenceCategoryHeader(title = "CONSOLE OUTPUT")
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F141C))
                    .padding(14.dp)
            ) {
                // Header bar of terminal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shell Session",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFE2E2E6)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (state.terminalLastCode != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (state.terminalLastCode == 0) StatusSuccess.copy(alpha = 0.2f) else StatusError.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Code ${state.terminalLastCode}",
                                color = if (state.terminalLastCode == 0) StatusSuccess else StatusError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Shell Output", state.terminalOutput))
                            Toast.makeText(context, "Output copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Output",
                            tint = Color(0xFFB0BEC5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Monospace text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(terminalHScroll)
                ) {
                    Text(
                        text = state.terminalOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF81C784),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
