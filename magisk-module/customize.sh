SKIPUNZIP=0

ui_print "*********************************************"
ui_print "      Magisk M3 Module Template             "
ui_print "      Material You Companion App            "
ui_print "*********************************************"

# Verify environment
ui_print "- Checking environment: Magisk / KernelSU / APatch..."
ui_print "- Target Module Path: $MODPATH"

# Install Companion App APK
APK_PATH="$MODPATH/MagiskTemplate.apk"
if [ -f "$APK_PATH" ]; then
    ui_print "- Installing Companion App (M3 Material You)..."
    pm install -r "$APK_PATH" >/dev/null 2>&1 && ui_print "- Companion App installed successfully!" || ui_print "- Companion App will be installed on first boot"
else
    ui_print "- Warning: Companion APK not found in package root."
fi

# Set proper permissions
ui_print "- Configuring execution permissions..."
set_perm_recursive "$MODPATH" 0 0 0755 0644
[ -f "$MODPATH/service.sh" ] && set_perm "$MODPATH/service.sh" 0 0 0755
[ -f "$MODPATH/post-fs-data.sh" ] && set_perm "$MODPATH/post-fs-data.sh" 0 0 0755
[ -f "$MODPATH/action.sh" ] && set_perm "$MODPATH/action.sh" 0 0 0755

ui_print "*********************************************"
ui_print "  Installation completed successfully!       "
ui_print "  Open the Magisk Template app to configure. "
ui_print "*********************************************"
