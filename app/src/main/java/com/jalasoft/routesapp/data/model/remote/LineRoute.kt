package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointListToLocationList
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointToLocation
import java.io.Serializable
import java.util.*

data class LineRoute(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val line: DocumentReference? = null,
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val routePoints: List<GeoPoint> = listOf(),
    val stops: List<GeoPoint> = listOf(),
    val color: String = "",
    val averageVelocity: String = "0.0",
    @ServerTimestamp
    val createAt: Date? = Date(),
    @ServerTimestamp
    val updateAt: Date? = Date()
) : Serializable {
    fun lineRouteToLineRouteInfo(): LineRouteInfo {
        val routePoints = geoPointListToLocationList(routePoints)
        val start = start?.let { geoPointToLocation(it) }
        val end = end?.let { geoPointToLocation(it) }
        val stops = geoPointListToLocationList(stops)

        return LineRouteInfo(id, name, idLine, routePoints, start, end, stops, color, averageVelocity.toDouble(), createAt?.time ?: 0, updateAt?.time ?: 0)
    }
}

data class LineRouteInfo(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf(),
    val color: String = "",
    val averageVelocity: Double = 0.0,
    val createAt: Long = 0,
    val updateAt: Long = 0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Location.CREATOR) ?: listOf(),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.createTypedArrayList(Location.CREATOR) ?: listOf(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(idLine)
        parcel.writeTypedList(routePoints)
        parcel.writeParcelable(start, flags)
        parcel.writeParcelable(end, flags)
        parcel.writeTypedList(stops)
        parcel.writeString(color)
        parcel.writeDouble(averageVelocity)
        parcel.writeLong(createAt)
        parcel.writeLong(updateAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LineRouteInfo> {
        override fun createFromParcel(parcel: Parcel): LineRouteInfo {
            return LineRouteInfo(parcel)
        }

        override fun newArray(size: Int): Array<LineRouteInfo?> {
            return arrayOfNulls(size)
        }
    }
}
