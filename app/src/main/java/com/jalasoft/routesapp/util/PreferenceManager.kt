package com.jalasoft.routesapp.util

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.jalasoft.routesapp.R

object PreferenceManager {
    private const val PREFERENCES_KEY = "routes_app_preferences"
    private const val CURRENT_CITY_LAT = "current_city_lat"
    private const val CURRENT_CITY_LNG = "current_city_lng"
    private const val CURRENT_CITY = "current_city"
    private const val CURRENT_CITY_ID = "current_city_id"
    const val NOT_FOUND = "null"

    fun saveCurrentLocation(context: Context, valueLat: String, valueLng: String, cityName: String, cityID: String) {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        pref.edit().putString(CURRENT_CITY_LAT, valueLat).apply()
        pref.edit().putString(CURRENT_CITY_LNG, valueLng).apply()
        pref.edit().putString(CURRENT_CITY, cityName).apply()
        pref.edit().putString(CURRENT_CITY_ID, cityID).apply()
    }

    fun getCurrentLocationLat(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY_LAT, NOT_FOUND) ?: NOT_FOUND
    }

    fun getCurrentLocationLng(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY_LNG, NOT_FOUND) ?: NOT_FOUND
    }

    fun getCurrentCity(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY, NOT_FOUND) ?: NOT_FOUND
    }

    fun deleteAllData(context: Context) {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
    }

    fun getCurrentCityID(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY_ID, NOT_FOUND) ?: NOT_FOUND
    }

    fun getAmplitude(context: Context): Amplitude {
        return Amplitude(
            Configuration(
                apiKey = context.getString(R.string.amplitude_api_key),
                context = context.applicationContext
            )
        )
    }
}
