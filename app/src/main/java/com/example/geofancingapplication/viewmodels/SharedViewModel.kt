package com.example.geofancingapplication.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geofancingapplication.data.DataStoreRepository
import com.example.geofancingapplication.data.GeofenceEntity
import com.example.geofancingapplication.data.GeofenceRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
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

    //data store
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()

    fun saveFirstLaunch(firstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveFirstLaunch(firstLaunch)
        }
    }

    //database
    val readGeofences = geofenceRepository.readGeofences.asLiveData()

    private fun addGeofence(geofenceEntity: GeofenceEntity) {
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