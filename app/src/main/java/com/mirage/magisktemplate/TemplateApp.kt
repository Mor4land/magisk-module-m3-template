package com.mirage.magisktemplate

import android.app.Application
import android.util.Log
import com.topjohnwu.superuser.Shell

class TemplateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Configure Libsu - Official Magisk Root Shell library by topjohnwu
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER)
                .setTimeout(15)
        )
        Log.i("MagiskTemplate", "Libsu pre-configured with FLAG_MOUNT_MASTER")
    }
}
