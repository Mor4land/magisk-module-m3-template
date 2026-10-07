package com.mirage.magisktemplate.core.model

enum class RootManager(val displayName: String) {
    MAGISK("Magisk"),
    KERNELSU("KernelSU"),
    APATCH("APatch"),
    UNKNOWN("Rooted (Generic)"),
    NONE("No Root Access")
}

data class RootInfo(
    val isRooted: Boolean = false,
    val manager: RootManager = RootManager.NONE,
    val managerVersion: String = "N/A",
    val managerCode: Int = 0,
    val seLinuxMode: String = "Unknown",
    val suBinaryPath: String = "N/A"
)
