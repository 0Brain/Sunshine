package com.prasan.sunshine.utils

import android.content.Context
import android.net.Uri
import com.prasan.sunshine.data.SunshinePreferences
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class NetworkUtils {
    companion object{

        const val DYNAMIC_WEATHER_URL = "https://andfun-weather.udacity.com/weather"

        private const val STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather"

        private const val FORECAST_BASE_URL = STATIC_WEATHER_URL

        /*
         * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
         * server. They are simply here to allow us to teach you how to build a URL if you were to use
         * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
         * we are not going to show you how to do so in this course.
         */

        /*
         * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
         * server. They are simply here to allow us to teach you how to build a URL if you were to use
         * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
         * we are not going to show you how to do so in this course.
         */
        /* The format we want our API to return */
        private const val format = "json"

        /* The units we want our API to return */
        private const val units = "metric"

        /* The number of days we want our API to return */
        private const val numDays = 14

        private const val QUERY_PARAM = "q"
        private const val LAT_PARAM = "lat"
        private const val LON_PARAM = "lon"
        private const val FORMAT_PARAM = "mode"
        private const val UNITS_PARAM = "units"
        private const val DAYS_PARAM = "cnt"


        /**
         * Retrieves the proper URL to query for the weather data. The reason for both this method as
         * well as {@link #buildUrlWithLocationQuery(String)} is two fold.
         * <p>
         * 1) You should be able to just use one method when you need to create the URL within the
         * app instead of calling both methods.
         * 2) Later in Sunshine, you are going to add an alternate method of allowing the user
         * to select their preferred location. Once you do so, there will be another way to form
         * the URL using a latitude and longitude rather than just a location String. This method
         * will "decide" which URL to build and return it.
         *
         * @param context used to access other Utility methods
         * @return URL to query weather service
         */
        fun getUrl(context:Context):URL{
            return if(SunshinePreferences.isLocationLatLonAvailable(context)){
                val preferredCoordinates:DoubleArray? = SunshinePreferences.getLocationCoordinates(context)
                val latitude:Double = preferredCoordinates!![0]
                val longitude:Double = preferredCoordinates[1]
                buildUrlFromLatLong(latitude,longitude)
            }else{
                val locationQuery:String? = SunshinePreferences.getPreferredWeatherLocation(context)
                buildUrlFromLocationQuery(locationQuery!!)
            }
        }

        private fun buildUrlFromLatLong(latitude:Double, longitude:Double):URL{
            val uri: Uri? =  Uri.parse(FORECAST_BASE_URL)
                .buildUpon()
                .appendQueryParameter(LAT_PARAM, latitude.toString())
                .appendQueryParameter(LON_PARAM, longitude.toString())
                .appendQueryParameter(FORMAT_PARAM,format)
                .appendQueryParameter(UNITS_PARAM,units)
                .appendQueryParameter(DAYS_PARAM, numDays.toString())
                .build()

            var url:URL? = null
            try {
                url = URL(uri.toString())
            }catch (e:IOException){
                e.printStackTrace()
            }
            return url!!
        }

        private fun buildUrlFromLocationQuery(locationQuery:String):URL{
            val uri: Uri? =  Uri.parse(FORECAST_BASE_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAM,locationQuery)
                .appendQueryParameter(FORMAT_PARAM,format)
                .appendQueryParameter(UNITS_PARAM,units)
                .appendQueryParameter(DAYS_PARAM, numDays.toString())
                .build()

            var url:URL? = null
            try {
                url = URL(uri.toString())
            }catch (e:IOException){
                e.printStackTrace()
            }
            return url!!
        }



        @Throws(IOException::class)
        fun getResponseFromHttpUrl(url: URL):String?{
            val urlConnection:HttpURLConnection = url.openConnection() as HttpURLConnection
            try {
                 val inStream:InputStream = urlConnection.inputStream
                 val scanner: Scanner = Scanner(inStream)
                scanner.useDelimiter("\\A")
                val hasInput:Boolean = scanner.hasNext()
                return if (hasInput){
                    scanner.next()
                }else{
                    null
                }
            }finally {
                urlConnection.disconnect()
            }
        }

    }
}