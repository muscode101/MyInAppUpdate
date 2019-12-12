package com.muscode.myinappupdater

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.muscode.myinappupdater.util.UpdateUtil


class MainActivity : AppCompatActivity() {


    private lateinit var updateUtil: UpdateUtil
    private lateinit var path: String

    private val allPermission = 1001
    private val installerRequestCode = 1002
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/app-debug.apk"

        if (hasPermissions()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!packageManager.canRequestPackageInstalls()) {

                    startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                            Uri.parse(String.format("package:%s", packageName))
                        ), installerRequestCode
                    )
                } else {
                    checkUpdates()
                }
            } else {
                checkUpdates()
            }

        } else {
            requestPermissions()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == installerRequestCode && resultCode == Activity.RESULT_OK) {
            if (packageManager.canRequestPackageInstalls()) {
                checkUpdates()
            }
        }
    }

    private fun checkUpdates() {
        val jsonUri = getString(R.string.jsonUri)
        updateUtil = UpdateUtil(jsonUri)
        if (updateUtil.isUpdateAvailable) {
            startUpdateActivity()
        }
    }

    private fun startUpdateActivity() {
        val myIntent = Intent(this, UpdateActivity::class.java)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(myIntent)
    }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(this, permissions, allPermission)

    private fun hasPermissions(): Boolean =
        permissions.none {
            ActivityCompat.checkSelfPermission(
                this, it
            ) != PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            allPermission -> {
                val isPermissionGranted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (isPermissionGranted) {
                    checkUpdates()
                }
            }
        }
    }
}