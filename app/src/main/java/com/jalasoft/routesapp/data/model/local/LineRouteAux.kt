package com.jalasoft.routesapp.data.model.local

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class LineRouteAux(
    val lineId: String,
    val routeId: String = "",
    var name: String = "",
    var color: String = "",
    var velocity: Double = 0.0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lineId)
        parcel.writeString(routeId)
        parcel.writeString(name)
        parcel.writeString(color)
        parcel.writeDouble(velocity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LineRouteAux> {
        override fun createFromParcel(parcel: Parcel): LineRouteAux {
            return LineRouteAux(parcel)
        }

        override fun newArray(size: Int): Array<LineRouteAux?> {
            return arrayOfNulls(size)
        }
    }
}
