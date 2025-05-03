package com.example.geofancingapplication.data

import android.graphics.Bitmap
import android.os.Parcelable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.geofancingapplication.util.Constants.DATABASE_TABLE_NAME
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = DATABASE_TABLE_NAME)
@Parcelize
class GeofenceEntity(
    var geoId: Long,
    var name: String,
    var location: String,
    var latitude: Double,
    var longitude: Double,
    var radius: Float,
    var snapshot: Bitmap
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}