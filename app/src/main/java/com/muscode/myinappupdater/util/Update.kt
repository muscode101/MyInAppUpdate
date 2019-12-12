package com.muscode.myinappupdater.util

import java.net.URL

data class Update (var latestVersion: String? = null,
    var latestVersionCode: Int? = null,
    var releaseNotes: String? = null,
    var urlToDownload: URL? = null
    )