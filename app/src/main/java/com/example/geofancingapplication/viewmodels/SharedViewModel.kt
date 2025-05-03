package com.example.geofancingapplication.viewmodels

import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geofancingapplication.broadcastReceiver.GeofenceBroadcastReceiver
import com.example.geofancingapplication.data.DataStoreRepository
import com.example.geofancingapplication.data.GeofenceEntity
import com.example.geofancingapplication.data.GeofenceRepository
import com.example.geofancingapplication.util.Permissions
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SharedViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val geofenceRepository: GeofenceRepository
) : AndroidViewModel(application) {
    val app = application
    private var geofencingClient = LocationServices.getGeofencingClient(app.applicationContext)

    var geoID = 0L
    var geoName = "Default"
    var geoCountryCode = ""

    var geoLocationName = "Search a city"
    var geoLatLng = LatLng(0.0, 0.0)
    var geoRadius = 500.0f
    var geoSnapshot: Bitmap? = null

    var geoCitySelected = false
    var geofenceReady = false
    var geofencePrepared = false

    fun resetSharedValues() {
        var geoID = 0L
        var geoName = "Default"
        var geoCountryCode = ""
        var geoLocationName = "Search a city"
        var geoLatLng = LatLng(0.0, 0.0)
        var geoRadius = 500.0f
        var geoSnapshot = null

        var geoCitySelected = false
        var geofenceReady = false
        var geofencePrepared = false
    }

    //data store
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()

    fun saveFirstLaunch(firstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveFirstLaunch(firstLaunch)
        }
    }

    //database
    val readGeofences = geofenceRepository.readGeofences.asLiveData()

    fun addGeofence(geofenceEntity: GeofenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.addGeofence(geofenceEntity)
        }
    }

    fun deleteGeofence(geofenceEntity: GeofenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.deleteGeofence(geofenceEntity)
        }
    }

    fun addGeofenceToDatabase(location: LatLng) {
        val geofenceEntity = GeofenceEntity(
            geoID,
            geoName,
            geoLocationName,
            location.latitude,
            location.longitude,
            geoRadius,
            geoSnapshot!!
        )
        addGeofence(geofenceEntity)
    }

    private fun setPendingIntent(geoID: Int): PendingIntent {
        val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            app,
            geoID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
    }

    @Suppress("MissingPermission")
    fun startGeofence(
        lat: Double,
        long: Double
    ) {
        if (Permissions.hasBackgroundLocationPermission(app)) {
            val geofence = Geofence.Builder()
                .setRequestId(geoID.toString())
                .setCircularRegion(
                    lat,
                    long,
                    geoRadius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or
                            Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .setLoiteringDelay(5000)
                .build()
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or
                            Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .addGeofence(geofence)
                .build()
            geofencingClient.addGeofences(geofencingRequest, setPendingIntent(geoID.toInt()))
                .run {
                    addOnSuccessListener {
                        Log.d("Geofence", "Successfully Added")
                    }
                    addOnFailureListener {
                        Log.d("Geofence", it.message.toString())
                    }
                }
        } else {
            Log.d("Geofence", "Permission not granted")
        }
    }

    suspend fun stopGeofence(geoIDs: List<Long>): Boolean {
        return if (Permissions.hasBackgroundLocationPermission(app)) {
            val result = CompletableDeferred<Boolean>()
            geofencingClient.removeGeofences(setPendingIntent(geoIDs.first().toInt()))
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.complete(true)
                    } else {
                        result.complete(false)
                    }
                }
            result.await()
        } else {
            return false
        }
    }

    fun getBounds(center: LatLng, radius: Float): LatLngBounds {
        val distanceFromCenterCorner = radius * sqrt(2.0)
        val southWestCorner = SphericalUtil
            .computeOffset(center, distanceFromCenterCorner, 255.0)
        val northEastCorner = SphericalUtil
            .computeOffset(center, distanceFromCenterCorner, 45.0)
        return LatLngBounds(southWestCorner, northEastCorner)
    }

    fun checkDeviceLocationSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(LocationManager::class.java)
            locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }

    }
}