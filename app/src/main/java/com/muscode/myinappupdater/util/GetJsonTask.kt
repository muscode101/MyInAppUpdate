package com.muscode.myinappupdater.util

import android.os.AsyncTask

class GetJsonTask(var jsonUri: String) : AsyncTask<String?, Void?, Update?>() {

    override fun doInBackground(vararg p0: String?): Update? = try {
        val parserJSON = ParserJSON(jsonUri)
        val response = parserJSON.parse()
        response
    } catch (e: Exception) {
        Update()
    }
}