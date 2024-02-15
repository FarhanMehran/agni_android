package com.agnidating.agni.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/*fun AppCompatActivity.requestPermission(vararg permissions:String,onPermissionResult:(Boolean,msg:String)->Unit ){
    val launcher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        var result:Boolean=true
        it.entries.forEach {
            if (it.value==false){
                result=false
                onPermissionResult(false,"${it.key} permission Denied")
            }
        }
        if (result){
            onPermissionResult(true,"Permission Granted")
        }
    }
    val granted=ContextCompat.checkSelfPermission(this,permissions[0])==PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,permissions[1])==PackageManager.PERMISSION_GRANTED
    if (granted.not())
     launcher.launch(permissions)
    else
        onPermissionResult(true,"Permission Granted")
}*/


fun Fragment.requestPermission(permissions:Array<String>, onPermissionResult:(Boolean, msg:String)->Unit ){
    val launcher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        var result=true
        it.entries.forEach {
            if (it.value==false){
                result=false
                onPermissionResult(false,"${it.key} permission Denied")
            }
        }
        if (result){
            onPermissionResult(true,"Permission Granted")
        }
    }
    val granted=ContextCompat.checkSelfPermission(requireContext(),permissions[0])==PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(requireContext(),permissions[1])==PackageManager.PERMISSION_GRANTED
    if (granted.not())
     launcher.launch(permissions)
    else
        onPermissionResult(true,"Permission Granted")
}