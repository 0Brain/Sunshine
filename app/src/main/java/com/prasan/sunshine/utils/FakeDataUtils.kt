package com.prasan.sunshine.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.prasan.sunshine.data.WeatherContract.WeatherEntry
import java.util.concurrent.TimeUnit


class FakeDataUtils {
    companion object{
        private val weatherIDs = intArrayOf(200, 300, 500, 711, 900, 962)

        /**
         * Creates a single ContentValues object with random weather data for the provided date
         * @param date a normalized date
         * @return ContentValues object filled with random weather data
         */
        private fun createTestWeatherContentValues(date: Long): ContentValues {
            val testWeatherValues = ContentValues()
            testWeatherValues.put(WeatherEntry.COLUMN_DATE, date)
            testWeatherValues.put(WeatherEntry.COLUMN_DEGREES, Math.random() * 2)
            testWeatherValues.put(WeatherEntry.COLUMN_HUMIDITY, Math.random() * 100)
            testWeatherValues.put(WeatherEntry.COLUMN_PRESSURE, 870 + Math.random() * 100)
            val maxTemp = (Math.random() * 100).toInt()
            testWeatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, maxTemp)
            testWeatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, maxTemp - (Math.random() * 10).toInt())
            testWeatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, Math.random() * 10)
            testWeatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherIDs[((Math.random()*10)%5).toInt()]);
            return testWeatherValues
        }

        /**
         * Creates random weather data for 7 days starting today
         * @param context
         */
        fun insertFakeData(context: Context) {
            //Get today's normalized date
            val today = SunshineDateUtils.normalizeDate(System.currentTimeMillis())
            val fakeValues: MutableList<ContentValues> = ArrayList()
            //loop over 7 days starting today onwards
            for (i in 0 until 7) {
                Log.d("FakeUtils iterations","$i")
                fakeValues.add(createTestWeatherContentValues(today + TimeUnit.DAYS.toMillis(i.toLong())))
            }
            // Bulk Insert our new weather data into Sunshine's Database
            context.contentResolver.bulkInsert(WeatherEntry.CONTENT_URI, fakeValues.toTypedArray())
        }
    }
}