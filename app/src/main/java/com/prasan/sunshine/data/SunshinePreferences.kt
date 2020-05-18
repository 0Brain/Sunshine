package com.prasan.sunshine.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.prasan.sunshine.R


class SunshinePreferences {

    companion object{

        /*
     * Human readable location string, provided by the API.  Because for styling,
     * "Mountain View" is more recognizable than 94043.
     */
        const val PREF_CITY_NAME = "city_name"

        /*
     * In order to uniquely pinpoint the location on the map when we launch the
     * map intent, we store the latitude and longitude.
     */
        const val PREF_COORD_LAT = "coord_lat"
        const val PREF_COORD_LONG = "coord_long"

        /*
     * Before you implement methods to return your REAL preference for location,
     * we provide some default values to work with.
     */
        private const val DEFAULT_WEATHER_LOCATION = "94043,UK"
        private val DEFAULT_WEATHER_COORDINATES =
            doubleArrayOf(37.4284, 122.0724)

        private const val DEFAULT_MAP_LOCATION =
            "1600 Amphitheatre Parkway, Mountain View, CA 94043"

        /**
         * Helper method to handle setting location details in Preferences (City Name, Latitude,
         * Longitude)
         *
         * @param c        Context used to get the SharedPreferences
         * @param cityName A human-readable city name, e.g "Mountain View"
         * @param lat      The latitude of the city
         * @param lon      The longitude of the city
         */
        fun setLocationDetails(
            c: Context?,
            cityName: String?,
            lat: Double,
            lon: Double
        ) {
            /** This will be implemented in a future lesson  */
        }

        /**
         * Helper method to handle setting location details in Preferences (city name, latitude,
         * longitude)
         *
         *
         * When the location details are updated, the database should to be cleared.
         *
         * @param context  Context used to get the SharedPreferences
         * @param lat      the latitude of the city
         * @param lon      the longitude of the city
         */
        fun setLocationDetails(
            context: Context?,
            lat: Double,
            lon: Double
        ) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sp.edit()
            editor.putLong(
                PREF_COORD_LAT,
                java.lang.Double.doubleToRawLongBits(lat)
            )
            editor.putLong(
                PREF_COORD_LONG,
                java.lang.Double.doubleToRawLongBits(lon)
            )
            editor.apply()
        }

        /**
         * Helper method to handle setting a new location in preferences.  When this happens
         * the database may need to be cleared.
         *
         * @param c               Context used to get the SharedPreferences
         * @param locationSetting The location string used to request updates from the server.
         * @param lat             The latitude of the city
         * @param lon             The longitude of the city
         */
        fun setLocation(
            c: Context?,
            locationSetting: String?,
            lat: Double,
            lon: Double
        ) {
            /** This will be implemented in a future lesson  */
        }

        /**
         * Resets the stored location coordinates.
         *
         * @param c Context used to get the SharedPreferences
         */
        fun resetLocationCoordinates(c: Context?) {
            /** This will be implemented in a future lesson  */
        }

        /**
         * Returns the location currently set in Preferences. The default location this method
         * will return is "94043,USA", which is Mountain View, California. Mountain View is the
         * home of the headquarters of the Googleplex!
         *
         * @param context Context used to get the SharedPreferences
         * @return Location The current user has set in SharedPreferences. Will default to
         * "94043,USA" if SharedPreferences have not been implemented yet.
         */
        fun getPreferredWeatherLocation(context: Context?): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val locationKey = context!!.getString(R.string.prefs_location_key)
            val locationDefaultValue = context.getString(R.string.pref_location_default)

            return sharedPreferences.getString(locationKey,locationDefaultValue)
        }

        /**
         * Returns true if the user has selected metric temperature display.
         *
         * @param context Context used to get the SharedPreferences
         * @return true If metric display should be used
         */
        fun isMetric(context: Context?): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val keyForUnits = context!!.getString(R.string.pref_unit_key)
            val defaultValueForUnits = context.getString(R.string.pref_units_value_metric)
            val preferredUnits = sharedPreferences.getString(keyForUnits,defaultValueForUnits)
            val metric = context.getString(R.string.pref_units_value_metric)
            val userPrefersMetric:Boolean
            userPrefersMetric = preferredUnits == metric
            return userPrefersMetric
        }

        private fun getDefaultWeatherLocation(): String? {
            /** This will be implemented in a future lesson  */
            return DEFAULT_WEATHER_LOCATION
        }

        fun getDefaultWeatherCoordinates(): DoubleArray? {
            /** This will be implemented in a future lesson  */
            return DEFAULT_WEATHER_COORDINATES
        }

        /**
         * Returns true if the latitude and longitude values are available. The latitude and
         * longitude will not be available until the lesson where the PlacePicker API is taught.
         *
         * @param context used to get the SharedPreferences
         * @return true if lat/long are saved in SharedPreferences
         */
        fun isLocationLatLonAvailable(context: Context?): Boolean {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val spContainLatitude = sp.contains(PREF_COORD_LAT)
            val spContainLongitude = sp.contains(PREF_COORD_LONG)
            var spContainBothLatitudeAndLongitude = false
            if (spContainLatitude && spContainLongitude) {
                spContainBothLatitudeAndLongitude = true
            }
            return spContainBothLatitudeAndLongitude
        }

        /**
         * Returns the location coordinates associated with the location. Note that there is a
         * possibility that these coordinates may not be set, which results in (0,0) being returned.
         * Interestingly, (0,0) is in the middle of the ocean off the west coast of Africa.
         *
         * @param context used to access SharedPreferences
         * @return an array containing the two coordinate values for the user's preferred location
         */
        fun getLocationCoordinates(context: Context?): DoubleArray? {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val preferredCoordinates = DoubleArray(2)

            /*
         * This is a hack we have to resort to since you can't store doubles in SharedPreferences.
         *
         * Double.doubleToLongBits returns an integer corresponding to the bits of the given
         * IEEE 754 double precision value.
         *
         * Double.longBitsToDouble does the opposite, converting a long (that represents a double)
         * into the double itself.
         */preferredCoordinates[0] = java.lang.Double
                .longBitsToDouble(
                    sp.getLong(
                        PREF_COORD_LAT,
                        java.lang.Double.doubleToRawLongBits(0.0)
                    )
                )
            preferredCoordinates[1] = java.lang.Double
                .longBitsToDouble(
                    sp.getLong(
                        PREF_COORD_LONG,
                        java.lang.Double.doubleToRawLongBits(0.0)
                    )
                )
            return preferredCoordinates
        }

    }
}