package com.example.geofancingapplication.broadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.geofancingapplication.R
import com.example.geofancingapplication.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.geofancingapplication.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.geofancingapplication.util.Constants.NOTIFICATION_ID
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("Broadcast receiver", errorMessage)
            return
        }
        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d("Broadcast receiver", "User entered the geofence")
                displayNotification(context, "User entered the geofence")
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.d("Broadcast receiver", "User dwelling inside the geofence")
                displayNotification(context, "User dwelling inside the geofence")
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d("Broadcast receiver", "User exited the geofence")
                displayNotification(context, "User exited the geofence")
            }

            else -> {
                Log.e("Broadcast receiver", "Unknown transition")
                displayNotification(context, "Unknown transition")
            }
        }
    }

    private fun displayNotification(context: Context, geofenceTransition: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Geofence")
            .setContentText(geofenceTransition)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}