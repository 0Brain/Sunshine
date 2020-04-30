package com.prasan.sunshine.utils

import android.net.Uri
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class NetworkUtils {
    companion object{
        fun buildUrl(locationQuery:String):URL{
            val uri: Uri? =  Uri.parse(AppConstants.FORECAST_BASE_URL)
                .buildUpon()
                .appendQueryParameter(AppConstants.QUERY_PARAM,locationQuery)
                .appendQueryParameter(AppConstants.FORMAT_PARAM,AppConstants.format)
                .appendQueryParameter(AppConstants.UNITS_PARAM,AppConstants.units)
                .appendQueryParameter(AppConstants.DAYS_PARAM, AppConstants.numDays.toString())
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
                var hasInput:Boolean = scanner.hasNext()
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