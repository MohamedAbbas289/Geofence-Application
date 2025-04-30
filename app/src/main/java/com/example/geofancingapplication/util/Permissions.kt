package com.example.geofancingapplication.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.example.geofancingapplication.util.Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import com.example.geofancingapplication.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.example.geofancingapplication.util.Constants.PERMISSION_NOTIFICATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {
    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun requestLocationPermission(fragment: Fragment) {
        EasyPermissions.requestPermissions(
            fragment,
            "This Application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return true
    }

    fun requestBackgroundLocationPermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                fragment,
                "Background Location Permission is Essential to this Application. Without it we will not be able to Provide you with our Services.",
                PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
        return true
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                activity,
                "Notification Permission is Essential to this Application. Without it the application may not work in the best way.",
                PERMISSION_NOTIFICATION_REQUEST_CODE,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}