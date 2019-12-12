package com.muscode.myinappupdater.util

import com.muscode.myinappupdater.BuildConfig

class UpdateUtil(var jsonUri:String) {
    var isUpdateAvailable = checkUpdates()

    private fun checkUpdates(): Boolean {
        val appUpdateTask = AppUpdateTask(jsonUri)
        val versioncode = appUpdateTask.execute()
        return !versioncode.get().equals(BuildConfig.VERSION_CODE.toString())
    }

}