#!/system/bin/sh
# Magisk M3 Module Template - post-fs-data.sh
# Executed before Zygote starts, in post-fs-data mode

MODDIR=${0%/*}

# Ensure log directory and initial config exist
[ -f "$MODDIR/config.sh" ] || cp "$MODDIR/config.default.sh" "$MODDIR/config.sh" 2>/dev/null
chmod 0644 "$MODDIR/config.sh" 2>/dev/null
