package com.prasan.sunshine.utils

import android.content.ContentValues
import android.content.Context
import com.prasan.sunshine.data.SunshinePreferences
import com.prasan.sunshine.data.WeatherContract
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection


class OpenWeatherJsonUtils {
    companion object{

        /* Location information */
        private const val OWM_CITY = "city"
        private const val OWM_COORD = "coord"

        /* Location coordinate */
        private const val OWM_LATITUDE = "lat"
        private const val OWM_LONGITUDE = "lon"

        /* Weather information. Each day's forecast info is an element of the "list" array */
        private const val OWM_LIST = "list"

        private const val OWM_PRESSURE = "pressure"
        private const val OWM_HUMIDITY = "humidity"
        private const val OWM_WINDSPEED = "speed"
        private const val OWM_WIND_DIRECTION = "deg"

        /* All temperatures are children of the "temp" object */
        private const val OWM_TEMPERATURE = "temp"

        /* Max temperature for the day */
        private const val OWM_MAX = "max"
        private const val OWM_MIN = "min"

        private const val OWM_WEATHER = "weather"
        private const val OWM_WEATHER_ID = "id"

        private const val OWM_MESSAGE_CODE = "cod"

        @Throws(JSONException::class)
        fun getSimpleWeatherStringsFromJson(context: Context, forecastJsonStr: String?): Array<String?> {

            /* String array to hold each day's weather String */

            /* String array to hold each day's weather String */
            val parsedWeatherData:Array<String?>


            val forecastJson = JSONObject(forecastJsonStr)

            /* Is there an error? */

            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                when (forecastJson.getInt(OWM_MESSAGE_CODE)) {
                    HttpURLConnection.HTTP_OK -> {
                    }
                    /* Location invalid */
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        null!!
                    /* Server probably down */
                    else ->
                        null!!
                }
            }

            val weatherArray: JSONArray = forecastJson.getJSONArray(OWM_LIST)

            parsedWeatherData = arrayOfNulls(weatherArray.length())

            val localDate = System.currentTimeMillis()
            val utcDate: Long = SunshineDateUtils.getUTCDateFromLocal(localDate)
            val startDay: Long = SunshineDateUtils.normalizeDate(utcDate)

            for (i in 0 until weatherArray.length()) {
                var date: String
                var highAndLow: String?

                /* These are the values that will be collected */
                var high: Double
                var low: Double
                var description: String?
                var weatherId: Int

                /* Get the JSON object representing the day */
                val dayForecast = weatherArray.getJSONObject(i)
             /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
                val dateTimeMillis: Long = startDay + SunshineDateUtils.DAY_IN_MILLIS * i
                date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false)

                /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
                val weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0)
                weatherId = weatherObject.getInt(OWM_WEATHER_ID)
                description = SunshineWeatherUtils.getStringForWeatherCondition(context,weatherId)

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
                highAndLow = SunshineWeatherUtils.formatHighLows(context, high, low)
                parsedWeatherData[i] = "$date - $description - $highAndLow"
            }

            return parsedWeatherData
        }

        @Throws(JSONException::class)
        fun getWeatherContentsFromJson(context: Context, forecastJsonStr: String?):Array<ContentValues?>{
            val forecastJson = JSONObject(forecastJsonStr)

            /* Is there an error? */

            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                when (forecastJson.getInt(OWM_MESSAGE_CODE)) {
                    HttpURLConnection.HTTP_OK -> {
                    }
                    /* Location invalid */
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        null!!
                    /* Server probably down */
                    else ->
                        null!!
                }
            }


            val jsonWeatherArray: JSONArray = forecastJson.getJSONArray(OWM_LIST)
            val cityJson = forecastJson.getJSONObject(OWM_CITY)

            val cityCoord = cityJson.getJSONObject(OWM_COORD)
            val cityLatitude:Double = cityCoord.getDouble(OWM_LATITUDE)
            val cityLongitude = cityCoord.getDouble(OWM_LONGITUDE)

            SunshinePreferences.setLocationDetails(context, cityLatitude, cityLongitude)

            val weatherContentValues = arrayOfNulls<ContentValues>(jsonWeatherArray.length())

            /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);
            val normalizedUtcStartDay: Long = SunshineDateUtils.getNormalizedUtcDateForToday()

            for(i in 0 until jsonWeatherArray.length()){
                var pressure: Double
                var humidity: Int
                var windSpeed: Double
                var windDirection: Double

                var high: Double
                var low: Double

                var weatherId: Int

                /* Get the JSON object representing the day */

                /* Get the JSON object representing the day */
                val dayForecast: JSONObject = jsonWeatherArray.getJSONObject(i)

                /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */

                /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
                val dateTimeMillis: Long = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * i

                pressure = dayForecast.getDouble(OWM_PRESSURE)
                humidity = dayForecast.getInt(OWM_HUMIDITY)
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED)
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION)

                /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */

                /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
                val weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0)

                weatherId = weatherObject.getInt(OWM_WEATHER_ID)

                /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary variable, temporary folder, temporary employee, or many
             * others, and is just a bad variable name.
             */

                /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary variable, temporary folder, temporary employee, or many
             * others, and is just a bad variable name.
             */
                val temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE)
                high = temperatureObject.getDouble(OWM_MAX)
                low = temperatureObject.getDouble(OWM_MIN)

                val weatherValues = ContentValues()
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low)
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId)

                weatherContentValues[i] = weatherValues
            }

            return weatherContentValues
            }

        }
    }