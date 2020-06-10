package com.prasan.sunshine.sync

import android.content.ContentValues
import android.content.Context
import com.prasan.sunshine.data.WeatherContract
import com.prasan.sunshine.utils.NetworkUtils
import com.prasan.sunshine.utils.OpenWeatherJsonUtils
import java.net.URL


class SunshineSyncTask {
    companion object {
        @Synchronized
        fun syncWeather(context: Context?) {

            try {
                /*
                * The getUrl method will return the URL that we need to get the forecast JSON for the
                * weather. It will decide whether to create a URL based off of the latitude and
                * longitude or off of a simple location as a String.
                */
                val weatherRequestUrl: URL = NetworkUtils.getUrl(context!!)

                /* Use the URL to retrieve the JSON */
                val getJsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)

                /* Parse the JSON into a list of weather values */

                /* Parse the JSON into a list of weather values */
                val weatherValues: Array<ContentValues?> =
                    OpenWeatherJsonUtils.getWeatherContentValuesFromJson(
                        context,
                        getJsonWeatherResponse
                    )

                /*
                * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
                * would have returned null. We need to check for those cases here to prevent any
                * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
                * there isn't any to insert.
                */

                if(weatherValues!=null && weatherValues.isNotEmpty()){
                    val weatherContentResolver = context.contentResolver
                    /* Delete old weather data because we don't need to keep multiple days' data */
                    weatherContentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,null,null)
                    /* Insert our new weather data into Sunshine's ContentProvider */
                    weatherContentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,weatherValues)
                    /* If the code reaches this point, we have successfully performed our sync */
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }
}