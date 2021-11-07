package com.example.rsestok.utilits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val READ_CONTACT = Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
const val PERMISSION_REQUIRED = 200
const val WRITE_FILES = Manifest.permission.WRITE_EXTERNAL_STORAGE
const val CAMERA = Manifest.permission.CAMERA




fun checkPermission(permission:String):Boolean{
    return if (Build.VERSION.SDK_INT >= 23
            && ContextCompat.checkSelfPermission(APP_ACTIVITY, permission)!= PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission), PERMISSION_REQUIRED)
        false
    } else true
}