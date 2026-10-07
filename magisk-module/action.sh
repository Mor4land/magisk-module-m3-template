#!/system/bin/sh
# Magisk M3 Module Template - action.sh
# Triggered via Magisk/KernelSU Manager action button or via Companion App

MODDIR=${0%/*}
LOGFILE="$MODDIR/service.log"

echo "=== Magisk M3 Template Action Triggered ==="
echo "Date: $(date)"
echo "Module Path: $MODDIR"

if [ -f "$MODDIR/config.sh" ]; then
    . "$MODDIR/config.sh"
    echo "Current Configuration:"
    echo "  - FEATURE_ENABLED: $FEATURE_ENABLED"
    echo "  - BOOST_MODE: $BOOST_MODE"
    echo "  - ANIMATION_SCALE: $ANIMATION_SCALE"
    echo "  - TOUCH_SAMPLING_RATE: $TOUCH_SAMPLING_RATE"
else
    echo "Notice: config.sh does not exist yet."
fi

echo "Syncing filesystem caches..."
sync
echo "Action script completed successfully!"
