package com.mirage.magisktemplate.core.model

data class ModuleInfo(
    val id: String = "",
    val name: String = "",
    val version: String = "",
    val versionCode: Int = 0,
    val author: String = "",
    val description: String = "",
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = false,
    val isUpdatePending: Boolean = false,
    val isRemovePending: Boolean = false
)
