package com.mirage.magisktemplate.core.root

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ShellResult(
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>
) {
    val isSuccess: Boolean get() = exitCode == 0
    val text: String get() = stdout.joinToString("\n")
    val errorText: String get() = stderr.joinToString("\n")
}

object ShellManager {

    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            Shell.isAppGrantedRoot() == true || Shell.getShell().isRoot
        } catch (_: Exception) {
            false
        }
    }

    suspend fun exec(command: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(command).exec()
            ShellResult(
                exitCode = result.code,
                stdout = result.out ?: emptyList(),
                stderr = result.err ?: emptyList()
            )
        } catch (e: Exception) {
            ShellResult(
                exitCode = -1,
                stdout = emptyList(),
                stderr = listOf("Shell execution error: ${e.message ?: e.javaClass.simpleName}")
            )
        }
    }

    suspend fun execLines(vararg commands: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(*commands).exec()
            ShellResult(
                exitCode = result.code,
                stdout = result.out ?: emptyList(),
                stderr = result.err ?: emptyList()
            )
        } catch (e: Exception) {
            ShellResult(
                exitCode = -1,
                stdout = emptyList(),
                stderr = listOf("Batch shell error: ${e.message ?: e.javaClass.simpleName}")
            )
        }
    }

    suspend fun getSELinuxMode(): String = withContext(Dispatchers.IO) {
        val res = exec("getenforce")
        if (res.isSuccess && res.stdout.isNotEmpty()) {
            res.stdout[0].trim()
        } else {
            "Unknown"
        }
    }

    suspend fun restartSystemUI(): ShellResult = withContext(Dispatchers.IO) {
        exec("killall -9 com.android.systemui 2>/dev/null || pkill -9 -f com.android.systemui")
    }

    suspend fun softReboot(): ShellResult = withContext(Dispatchers.IO) {
        exec("setprop ctl.restart zygote || setprop ctl.restart zygote_secondary")
    }

    suspend fun rebootSystem(): ShellResult = withContext(Dispatchers.IO) {
        exec("svc power reboot || reboot")
    }

    suspend fun rebootRecovery(): ShellResult = withContext(Dispatchers.IO) {
        exec("svc power reboot recovery || reboot recovery")
    }

    suspend fun rebootBootloader(): ShellResult = withContext(Dispatchers.IO) {
        exec("svc power reboot bootloader || reboot bootloader")
    }

    suspend fun dropCaches(): ShellResult = withContext(Dispatchers.IO) {
        exec("sync && echo 3 > /proc/sys/vm/drop_caches")
    }
}
