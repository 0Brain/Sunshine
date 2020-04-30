package com.prasan.sunshine.utils

import android.content.Context

class SunshinePreferences {

    companion object{

        const val DEFAULT_WEATHER_LOCATION = "94043,USA"
        fun getPreferredWeatherLocation(context: Context): String {
            return getDefaultWeatherLocation()
        }

        private fun getDefaultWeatherLocation(): String {
            return DEFAULT_WEATHER_LOCATION
        }

    }
}