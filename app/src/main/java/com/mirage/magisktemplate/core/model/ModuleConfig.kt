package com.mirage.magisktemplate.core.model

object ModuleConfig {
    /**
     * Unique module ID matching id in module.prop
     * Change this single constant when adapting this template for a new module.
     */
    const val MODULE_ID = "mirage_magisk_template"

    const val MODULE_DISPLAY_NAME = "Magisk M3 Module Template"
    const val MODULE_AUTHOR = "Mirage Team"
    const val MODULE_VERSION = "v1.0.0"
    const val MODULE_VERSION_CODE = 1
    const val GITHUB_URL = "https://github.com/Mor4land/miragemodulesmagisk"
    const val SUPPORT_URL = "https://t.me/mirage_modules"

    // Standard Magisk / KernelSU / APatch module paths
    const val MODULES_ROOT = "/data/adb/modules"
    val MODULE_DIR = "$MODULES_ROOT/$MODULE_ID"
    val DISABLE_FLAG = "$MODULE_DIR/disable"
    val REMOVE_FLAG = "$MODULE_DIR/remove"
    val UPDATE_FLAG = "$MODULE_DIR/update"
    val PROP_FILE = "$MODULE_DIR/module.prop"
    val CONFIG_FILE = "$MODULE_DIR/config.sh"
    val SERVICE_LOG = "$MODULE_DIR/service.log"
    val ACTION_SCRIPT = "$MODULE_DIR/action.sh"
    val SYSTEM_PROP = "$MODULE_DIR/system.prop"
}
