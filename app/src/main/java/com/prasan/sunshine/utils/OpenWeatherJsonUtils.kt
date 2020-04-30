package com.prasan.sunshine.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.util.*


class OpenWeatherJsonUtils {
    companion object{
        @Throws(JSONException::class)
        fun getSimpleWeatherStringsFromJson(context: Context, forecastJsonStr: String?): Array<String?> {
            val OWM_LIST = "list"

            /* All temperatures are children of the "temp" object */

            /* All temperatures are children of the "temp" object */
            val OWM_TEMPERATURE = "temp"

            /* Max temperature for the day */

            /* Max temperature for the day */
            val OWM_MAX = "max"
            val OWM_MIN = "min"

            val OWM_WEATHER = "weather"
            val OWM_DESCRIPTION = "main"

            val OWM_MESSAGE_CODE = "cod"

            /* String array to hold each day's weather String */

            /* String array to hold each day's weather String */
            var parsedWeatherData:Array<String?>


            val forecastJson = JSONObject(forecastJsonStr)

            /* Is there an error? */

            /* Is there an error? */if (forecastJson.has(OWM_MESSAGE_CODE)) {
                val errorCode: Int = forecastJson.getInt(OWM_MESSAGE_CODE)
                when (errorCode) {
                    HttpURLConnection.HTTP_OK -> {
                    }
                    HttpURLConnection.HTTP_NOT_FOUND ->/* Location invalid */
                        return null!!
                    else ->/* Server probably down */
                        return null!!
                }
            }

            val weatherArray: JSONArray = forecastJson.getJSONArray(OWM_LIST)

            parsedWeatherData = arrayOfNulls(weatherArray.length())

            val localDate = System.currentTimeMillis()
            val utcDate: Long = SunshineDateUtils.getUTCDateFromLocal(localDate)
            val startDay: Long = SunshineDateUtils.normalizeDate(utcDate)

            for (i in 0 until weatherArray.length()) {
                var date: String
                var highAndLow: String

                /* These are the values that will be collected */
                var dateTimeMillis: Long
                var high: Double
                var low: Double
                var description: String

                /* Get the JSON object representing the day */
                val dayForecast = weatherArray.getJSONObject(i)
             /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
                dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i
                date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false)

                /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
                val weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0)
                description = weatherObject.getString(OWM_DESCRIPTION)

                /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary and is just a bad variable name.
             */
                val temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE)
                high = temperatureObject.getDouble(OWM_MAX)
                low = temperatureObject.getDouble(OWM_MIN)
                //highAndLow = SunshineWeatherUtils.formatHighLows(context, high, low)
                parsedWeatherData[i] = "$date - $description "
            }

            return parsedWeatherData
        }

    }
}