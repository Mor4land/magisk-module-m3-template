package com.mirage.magisktemplate.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirage.magisktemplate.core.model.ModuleSettings
import com.mirage.magisktemplate.ui.components.PreferenceCard
import com.mirage.magisktemplate.ui.components.PreferenceCategoryHeader
import com.mirage.magisktemplate.ui.components.PreferenceChips
import com.mirage.magisktemplate.ui.components.PreferenceSlider
import com.mirage.magisktemplate.ui.components.PreferenceSwitch
import com.mirage.magisktemplate.ui.components.PreferenceTextField
import com.mirage.magisktemplate.ui.viewmodel.MainUiState

@Composable
fun SettingsScreen(
    state: MainUiState,
    onUpdateSettings: ((ModuleSettings) -> ModuleSettings) -> Unit,
    onSaveSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val settings = state.settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Master Toggles
        PreferenceCategoryHeader(title = "GENERAL TWEAKS")
        PreferenceCard {
            PreferenceSwitch(
                title = "Enable Module Features",
                subtitle = "Master switch controlling all kernel & runtime hooks",
                icon = Icons.Default.Tune,
                checked = settings.featureEnabled,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(featureEnabled = checked) }
                }
            )

            PreferenceSwitch(
                title = "Verbose Logging",
                subtitle = "Output full execution trace to service.log",
                icon = Icons.Default.BugReport,
                checked = settings.verboseLogs,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(verboseLogs = checked) }
                }
            )
        }

        // Section 2: Governor & Boost Modes
        PreferenceCategoryHeader(title = "PERFORMANCE ENGINE")
        PreferenceCard {
            PreferenceChips(
                title = "Governor Profile",
                subtitle = "Adjust CPU/GPU scheduling and scaling governors",
                icon = Icons.Default.Speed,
                options = listOf(
                    "powersave" to "Power Save",
                    "balanced" to "Balanced",
                    "performance" to "Performance",
                    "extreme" to "Extreme"
                ),
                selectedOption = settings.boostMode,
                onOptionSelected = { mode ->
                    onUpdateSettings { it.copy(boostMode = mode) }
                }
            )

            PreferenceSlider(
                title = "Animation Speed Scale",
                subtitle = "Global transition and window animator scaling factor",
                icon = Icons.Default.ElectricBolt,
                value = settings.animationScale,
                valueRange = 0.25f..2.0f,
                steps = 6,
                valueFormat = { "${"%.2f".format(it)}x" },
                onValueChange = { scale ->
                    onUpdateSettings { it.copy(animationScale = scale) }
                }
            )
        }

        // Section 3: Hardware & Touch
        PreferenceCategoryHeader(title = "TOUCH & DISPLAY")
        PreferenceCard {
            PreferenceChips(
                title = "Touch Sampling Rate",
                subtitle = "Digitizer polling frequency for reduced input latency",
                icon = Icons.Default.TouchApp,
                options = listOf(
                    "120" to "120 Hz",
                    "240" to "240 Hz",
                    "360" to "360 Hz",
                    "480" to "480 Hz"
                ),
                selectedOption = settings.touchSamplingRate.toString(),
                onOptionSelected = { rateStr ->
                    onUpdateSettings { it.copy(touchSamplingRate = rateStr.toIntOrNull() ?: 240) }
                }
            )

            PreferenceSwitch(
                title = "Aggressive Doze Mode",
                subtitle = "Force deep sleep when screen turns off immediately",
                icon = Icons.Default.BatteryChargingFull,
                checked = settings.aggressiveDoze,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(aggressiveDoze = checked) }
                }
            )
        }

        // Section 4: Advanced
        PreferenceCategoryHeader(title = "ADVANCED & CUSTOM ARGUMENTS")
        PreferenceCard {
            PreferenceTextField(
                title = "Kernel / Shell Arguments",
                subtitle = "Passed directly to service.sh during boot initialization",
                icon = Icons.Default.Code,
                value = settings.customArgs,
                placeholder = "e.g. --governor=schedutil --boost-fps=120",
                onValueChange = { args ->
                    onUpdateSettings { it.copy(customArgs = args) }
                }
            )
        }

        // Save & Apply Button
        Button(
            onClick = onSaveSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.isSavingSettings && state.rootInfo.isRooted
        ) {
            if (state.isSavingSettings) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Writing to Root Storage...")
            } else {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Apply to config.sh")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
