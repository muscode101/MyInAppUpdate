package com.muscode.myinappupdater.util

import android.os.AsyncTask

class AppUpdateTask(var jsonUri: String) : AsyncTask<String?, Void?, String?>() {

    override fun doInBackground(vararg p0: String?): String? = try {

        val parserJSON = ParserJSON(jsonUri)
        val response = parserJSON.parse()
        response!!.latestVersion
    } catch (e: Exception) {
        e.message
    }
}