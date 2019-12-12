package com.muscode.myinappupdater.util

import com.muscode.myinappupdater.BuildConfig

class UpdateUtil(var jsonUri:String) {

    var isUpdateAvailable = checkUpdates()

    private fun checkUpdates(): Boolean {
        val jsonResponse = GetJsonTask(jsonUri).execute().get()!!
        val versioncode = jsonResponse.latestVersion

        return !versioncode.equals(BuildConfig.VERSION_CODE.toString())
    }
}