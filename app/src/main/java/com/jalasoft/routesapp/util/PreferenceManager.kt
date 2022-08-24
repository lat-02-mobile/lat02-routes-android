package com.jalasoft.routesapp.util

import android.content.Context

object PreferenceManager {
    private const val PREFERENCES_KEY = "routes_app_preferences"
    private const val CURRENT_CITY_LAT = "current_city_lat"
    private const val CURRENT_CITY_LNG = "current_city_lng"
    private const val CURRENT_CITY = "current_city"

    fun saveCurrentLocation(context: Context, valueLat: String, valueLng: String, city: String) {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        pref.edit().putString(CURRENT_CITY_LAT, valueLat).apply()
        pref.edit().putString(CURRENT_CITY_LNG, valueLng).apply()
        pref.edit().putString(CURRENT_CITY, city).apply()
    }

    fun getCurrentLocationLat(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY_LAT, "none") ?: "none"
    }

    fun getCurrentLocationLng(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY_LNG, "none") ?: "none"
    }

    fun getCurrentCity(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        return pref.getString(CURRENT_CITY, "none") ?: "none"
    }
}
