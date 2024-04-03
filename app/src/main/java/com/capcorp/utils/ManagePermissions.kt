package com.capcorp.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capcorp.R

class ManagePermissions(val activity: Activity) {
    val list = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // Check permissions at runtime
    fun checkPermissions(): Boolean {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
            return false
        } else {
            return true
        }
    }


    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }


    // Show alert dialog to request permissions
    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.need_permissions))
        builder.setMessage(activity.getString(R.string.some_permissions_required_task))
        builder.setPositiveButton(activity.getString(R.string.ok)) { _, which -> requestPermissions() }
        builder.setNeutralButton(activity.getString(R.string.cancel), null)
        val dialog = builder.create()
        dialog.show()
    }


    // Request the permissions at run time
    fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation asynchronously
            explain(activity.getString(R.string.manadatory_permissions_to_continue))
        } else {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), 100)
        }
    }


    // Process permissions result
    fun processPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }

    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage(msg)
            .setPositiveButton(activity.getString(R.string.yes)) { paramDialogInterface, paramInt ->
                paramDialogInterface.dismiss()

                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:com.here2dare")
                ).also {
                    activity.startActivity(it)
                }

            }
            .setNegativeButton(
                activity.getString(R.string.cancel)
            ) { paramDialogInterface, paramInt -> paramDialogInterface.dismiss() }
        dialog.show()
    }
}