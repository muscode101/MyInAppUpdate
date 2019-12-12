package com.muscode.myinappupdater.util

import com.muscode.myinappupdater.BuildConfig

class UpdateUtil(var jsonUri:String) {
    var isUpdateAvailable = checkUpdates()

    private fun checkUpdates(): Boolean {
        val getJsonTask = GetJsonTask(jsonUri)
        val versioncode = getJsonTask.execute().get()!!.latestVersion

        return !versioncode.equals(BuildConfig.VERSION_CODE.toString())
    }

}