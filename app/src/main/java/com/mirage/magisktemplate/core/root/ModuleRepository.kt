package com.mirage.magisktemplate.core.root

import com.mirage.magisktemplate.core.model.ModuleConfig
import com.mirage.magisktemplate.core.model.ModuleInfo
import com.mirage.magisktemplate.core.model.ModuleSettings
import com.mirage.magisktemplate.core.model.RootInfo
import com.mirage.magisktemplate.core.model.RootManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ModuleRepository {

    suspend fun getRootInfo(): RootInfo = withContext(Dispatchers.IO) {
        val isRooted = ShellManager.isRootAvailable()
        if (!isRooted) {
            return@withContext RootInfo(
                isRooted = false,
                manager = RootManager.NONE,
                managerVersion = "Not available",
                managerCode = 0,
                seLinuxMode = "Unknown",
                suBinaryPath = "Not found"
            )
        }

        val seLinux = ShellManager.getSELinuxMode()
        val suWhich = ShellManager.exec("which su").stdout.firstOrNull()?.trim() ?: "/system/bin/su"

        // 1. Check Magisk
        val magiskVerRes = ShellManager.exec("magisk -v")
        if (magiskVerRes.isSuccess && magiskVerRes.stdout.isNotEmpty()) {
            val ver = magiskVerRes.stdout.first().trim()
            val codeRes = ShellManager.exec("magisk -V")
            val code = codeRes.stdout.firstOrNull()?.trim()?.toIntOrNull() ?: 0
            return@withContext RootInfo(
                isRooted = true,
                manager = RootManager.MAGISK,
                managerVersion = ver,
                managerCode = code,
                seLinuxMode = seLinux,
                suBinaryPath = suWhich
            )
        }

        // 2. Check KernelSU
        val ksuRes = ShellManager.exec("ksud -V 2>/dev/null || cat /data/adb/ksu/version 2>/dev/null")
        if (ksuRes.isSuccess && ksuRes.stdout.isNotEmpty()) {
            val ver = ksuRes.stdout.first().trim()
            return@withContext RootInfo(
                isRooted = true,
                manager = RootManager.KERNELSU,
                managerVersion = ver,
                managerCode = 0,
                seLinuxMode = seLinux,
                suBinaryPath = suWhich
            )
        }

        // 3. Check APatch
        val apRes = ShellManager.exec("apd -V 2>/dev/null || cat /data/adb/ap/version 2>/dev/null")
        if (apRes.isSuccess && apRes.stdout.isNotEmpty()) {
            val ver = apRes.stdout.first().trim()
            return@withContext RootInfo(
                isRooted = true,
                manager = RootManager.APATCH,
                managerVersion = ver,
                managerCode = 0,
                seLinuxMode = seLinux,
                suBinaryPath = suWhich
            )
        }

        // 4. Generic Root
        RootInfo(
            isRooted = true,
            manager = RootManager.UNKNOWN,
            managerVersion = "Generic SU",
            managerCode = 0,
            seLinuxMode = seLinux,
            suBinaryPath = suWhich
        )
    }

    suspend fun getModuleInfo(): ModuleInfo = withContext(Dispatchers.IO) {
        val modDir = ModuleConfig.MODULE_DIR
        val dirCheck = ShellManager.exec("[ -d \"$modDir\" ] && echo 1 || echo 0")
        val isInstalled = dirCheck.stdout.firstOrNull()?.trim() == "1"

        if (!isInstalled) {
            return@withContext ModuleInfo(
                id = ModuleConfig.MODULE_ID,
                name = ModuleConfig.MODULE_DISPLAY_NAME,
                version = ModuleConfig.MODULE_VERSION,
                versionCode = ModuleConfig.MODULE_VERSION_CODE,
                author = ModuleConfig.MODULE_AUTHOR,
                description = "Module is not installed in ${ModuleConfig.MODULES_ROOT}",
                isInstalled = false,
                isEnabled = false
            )
        }

        val disableCheck = ShellManager.exec("[ -f \"${ModuleConfig.DISABLE_FLAG}\" ] && echo 1 || echo 0")
        val isEnabled = disableCheck.stdout.firstOrNull()?.trim() != "1"

        val updateCheck = ShellManager.exec("[ -f \"${ModuleConfig.UPDATE_FLAG}\" ] && echo 1 || echo 0")
        val isUpdatePending = updateCheck.stdout.firstOrNull()?.trim() == "1"

        val removeCheck = ShellManager.exec("[ -f \"${ModuleConfig.REMOVE_FLAG}\" ] && echo 1 || echo 0")
        val isRemovePending = removeCheck.stdout.firstOrNull()?.trim() == "1"

        // Parse module.prop
        val propRes = ShellManager.exec("cat \"${ModuleConfig.PROP_FILE}\" 2>/dev/null")
        var id = ModuleConfig.MODULE_ID
        var name = ModuleConfig.MODULE_DISPLAY_NAME
        var version = ModuleConfig.MODULE_VERSION
        var versionCode = ModuleConfig.MODULE_VERSION_CODE
        var author = ModuleConfig.MODULE_AUTHOR
        var description = "No description found in module.prop"

        if (propRes.isSuccess) {
            propRes.stdout.forEach { rawLine ->
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("#")) return@forEach
                val eq = line.indexOf('=')
                if (eq == -1) return@forEach
                val key = line.substring(0, eq).trim()
                val value = line.substring(eq + 1).trim()
                when (key) {
                    "id" -> id = value
                    "name" -> name = value
                    "version" -> version = value
                    "versionCode" -> versionCode = value.toIntOrNull() ?: versionCode
                    "author" -> author = value
                    "description" -> description = value
                }
            }
        }

        ModuleInfo(
            id = id,
            name = name,
            version = version,
            versionCode = versionCode,
            author = author,
            description = description,
            isInstalled = true,
            isEnabled = isEnabled,
            isUpdatePending = isUpdatePending,
            isRemovePending = isRemovePending
        )
    }

    suspend fun toggleModule(enable: Boolean): ShellResult = withContext(Dispatchers.IO) {
        val flag = ModuleConfig.DISABLE_FLAG
        if (enable) {
            ShellManager.exec("rm -f \"$flag\"")
        } else {
            ShellManager.exec("touch \"$flag\"")
        }
    }

    suspend fun loadSettings(): ModuleSettings = withContext(Dispatchers.IO) {
        val configFile = ModuleConfig.CONFIG_FILE
        val res = ShellManager.exec("cat \"$configFile\" 2>/dev/null")
        if (res.isSuccess && res.stdout.isNotEmpty()) {
            ModuleSettings.fromShellScript(res.stdout.joinToString("\n"))
        } else {
            ModuleSettings()
        }
    }

    suspend fun saveSettings(settings: ModuleSettings): ShellResult = withContext(Dispatchers.IO) {
        val modDir = ModuleConfig.MODULE_DIR
        val configFile = ModuleConfig.CONFIG_FILE
        val scriptContent = settings.toShellScript()

        val commands = arrayOf(
            "mkdir -p \"$modDir\"",
            "cat << 'EOF' > \"$configFile\"\n$scriptContent\nEOF",
            "chmod 0644 \"$configFile\""
        )
        ShellManager.execLines(*commands)
    }

    suspend fun runModuleAction(): ShellResult = withContext(Dispatchers.IO) {
        val actionScript = ModuleConfig.ACTION_SCRIPT
        val check = ShellManager.exec("[ -f \"$actionScript\" ] && echo 1 || echo 0")
        if (check.stdout.firstOrNull()?.trim() != "1") {
            return@withContext ShellResult(
                exitCode = -1,
                stdout = emptyList(),
                stderr = listOf("action.sh not found at $actionScript")
            )
        }
        ShellManager.exec("sh \"$actionScript\" 2>&1")
    }

    suspend fun readServiceLog(maxLines: Int = 150): String = withContext(Dispatchers.IO) {
        val logFile = ModuleConfig.SERVICE_LOG
        val res = ShellManager.exec("tail -n $maxLines \"$logFile\" 2>/dev/null")
        if (res.isSuccess && res.stdout.isNotEmpty()) {
            res.stdout.joinToString("\n")
        } else {
            "Log file is empty or not yet generated at $logFile"
        }
    }

    suspend fun clearServiceLog(): ShellResult = withContext(Dispatchers.IO) {
        val logFile = ModuleConfig.SERVICE_LOG
        ShellManager.exec("> \"$logFile\" 2>/dev/null || rm -f \"$logFile\"")
    }
}
