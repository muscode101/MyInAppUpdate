package com.muscode.myinappupdater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


class UpdateActivity : AppCompatActivity() {
    private var isDeleted = false
    private var apkDownloadId = 0L
    private var dm: DownloadManager? = null
    private val downloadedAppName = "NewUpdate.apk"
    private var downloadedApkPath = Environment.getExternalStorageDirectory().toString() + "/Download/" + downloadedAppName
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialog()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Latest Version is Available. Click on OK to update")
        builder.context.setTheme(R.style.AppTheme)
        builder.setPositiveButton("OK") { dialog, which ->
            Toast.makeText(this, "App Downloading...Please Wait", Toast.LENGTH_LONG).show()

            dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            deleteOldUpdate()
            downloadAndInstall()

        }
        builder.setNegativeButton("Remind Me Later") { dialog, which -> finish() }
        builder.show()
    }

    private fun deleteOldUpdate() {
        val file = File(downloadedApkPath)
        if (file.exists()) isDeleted = file.delete()
    }

    private fun downloadAndInstall() {
        downloadFile()
        initDownloadReceiver()
    }

    private fun downloadFile() {
        val apkUri =
            "https://firebasestorage.googleapis.com/v0/b/bur-io.appspot.com/o/Real%20Chess_v2.85_apkpure.com.apk?alt=media&token=a2003589-9f8e-416d-bde0-72685d0060c7"
        val downloadedAppName = "NewUpdate.apk"
        val request = DownloadManager.Request(Uri.parse(apkUri))

        request.setTitle("Download Apk Update")
        request.setDescription("download update for app")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            downloadedAppName
        )
        apkDownloadId = dm!!.enqueue(request)
    }

    private fun initDownloadReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_LONG)
                        .show()
                    val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                    if (downloadId == apkDownloadId) {
                        install()
                    }
                }
            }
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun install() {
        if (!isRooted) {
            noneRootedInstall()
        } else {
            rootedInstall()
        }
    }

    private fun noneRootedInstall() {
        val intentInstall = Intent(Intent.ACTION_VIEW)
        val newApkPath =
            Environment.getExternalStorageDirectory().toString() + "/Download/" + downloadedAppName
        val file = File(newApkPath)

        intentInstall.setDataAndType(
            FileProvider.getUriForFile(applicationContext, "$packageName.provider", file),
            "application/vnd.android.package-archive"
        )
        Log.d("phone path", newApkPath)
        intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intentInstall)
    }

    private fun rootedInstall() {
        Toast.makeText(
            applicationContext,
            "App Installing...Please Wait",
            Toast.LENGTH_LONG
        ).show()
        val file = File(downloadedApkPath)
        Log.d("IN INSTALLER:", downloadedApkPath)
        if (file.exists()) {
            try {
                Log.d("IN File exists:", downloadedApkPath)
                val command = "pm install -r $downloadedApkPath"
                Log.d("COMMAND:", command)
                val proc =
                    Runtime.getRuntime().exec(
                        arrayOf(
                            "su",
                            "-c",
                            command
                        )
                    )
                proc.waitFor()
                Toast.makeText(
                    applicationContext,
                    "App Installed Successfully",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) finish()
        return super.onKeyDown(keyCode, event)
    }

    private val isRooted: Boolean = findBinary()

    private fun findBinary(): Boolean {
        var found = false
        val places = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        for (where in places) {
            if (File(where + "su").exists()) {
                found = true
                break
            }
        }
        return found
    }
}