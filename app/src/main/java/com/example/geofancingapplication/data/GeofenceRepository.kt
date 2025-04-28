package com.example.geofancingapplication.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeofenceRepository @Inject constructor(
    private val geofenceDao: GeofenceDao
) {

    val readGeofences: Flow<MutableList<GeofenceEntity>> =
        geofenceDao.readGeofences()

    suspend fun addGeofence(geofenceEntity: GeofenceEntity) {
        geofenceDao.addGeofence(geofenceEntity)
    }

    suspend fun deleteGeofence(geofenceEntity: GeofenceEntity) {
        geofenceDao.deleteGeofence(geofenceEntity)
    }

}