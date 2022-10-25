package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointToLocation
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class TourPoint(
    val id: String? = "",
    val idCity: DocumentReference? = null,
    val name: String? = "",
    val address: String? = "",
    val destination: GeoPoint? = null,
    val urlImage: String? = "",
    val tourPointsCategoryRef: DocumentReference? = null,
    val categoryId: String? = "",
    val createAt: Timestamp = Timestamp.now(),
    val updateAt: Timestamp = Timestamp.now()
) : Serializable {

    suspend fun tourPointToTourPointLocal(): TourPointEntity {
        val dest = destination?.let { geoPointToLocation(it) }
        val lat = dest?.latitude ?: 0.0
        val lng = dest?.longitude ?: 0.0
        val destination = com.jalasoft.routesapp.data.model.local.Location(lat, lng)
        val mId = id ?: ""
        val mName = name ?: ""
        val mAddress = address ?: ""
        val mUrlImage = urlImage ?: ""
        var city: DocumentSnapshot?
        var cityId = ""
        idCity?.let { docRef ->
            city = docRef.get().await()
            city?.let {
                cityId = it.toObject(City::class.java)?.id ?: ""
            }
        }
        var category: DocumentSnapshot?
        var categoryName = ""
        var categoryIcon = ""
        tourPointsCategoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val targetCategory = it.toObject(TourPointsCategory::class.java)
                val currLang = Locale.getDefault().isO3Language
                categoryIcon = targetCategory?.icon ?: ""
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) targetCategory?.descriptionEsp ?: ""
                    else targetCategory?.descriptionEng ?: ""
            }
        }
        return TourPointEntity(mId, cityId, mName, mAddress, destination, mUrlImage, categoryIcon, categoryName, categoryId ?: "", createAt.toDate().time, updateAt.toDate().time)
    }
}

data class TourPointPath(
    val id: String? = "",
    val idCity: String? = "",
    val name: String? = "",
    val address: String? = "",
    val destination: Location? = null,
    val urlImage: String? = "",
    val categoryIcon: String? = "",
    val category: TourPointsCategory? = null,
    val categoryName: String? = "",
    val createAt: Long = 0,
    val updateAt: Long = 0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as TourPointsCategory?,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCity)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeParcelable(destination, flags)
        parcel.writeString(urlImage)
        parcel.writeString(categoryIcon)
        parcel.writeString(categoryName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TourPointPath> {
        override fun createFromParcel(parcel: Parcel): TourPointPath {
            return TourPointPath(parcel)
        }

        override fun newArray(size: Int): Array<TourPointPath?> {
            return arrayOfNulls(size)
        }
    }
}
