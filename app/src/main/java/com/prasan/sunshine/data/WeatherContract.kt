package com.prasan.sunshine.data

import android.net.Uri
import android.provider.BaseColumns
import com.prasan.sunshine.utils.SunshineDateUtils

class WeatherContract {
    companion object{
        /*
        * The "Content authority" is a name for the entire content provider, similar to the
        * relationship between a domain name and its website. A convenient string to use for the
        * content authority is the package name for the app, which is guaranteed to be unique on the
        * Play Store.
        */

        const val CONTENT_AUTHORITY = "com.prasan.sunshine"

        /*
         * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
         * the content provider for Sunshine.
        */
        val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

        /*
        * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
        * can handle. For instance,
        *
        *     content://com.prasan.sunshine/weather/
        *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
        *
        * is a valid path for looking at weather data.
        *
        *      content://com.example.android.sunshine/givemeroot/
        *
        * will fail, as the ContentProvider hasn't been given any information on what to do with
        * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
        */
        const val PATH_WEATHER = "weather"
    }
    class WeatherEntry:BaseColumns{
        companion object{
            /* The base CONTENT_URI used to query the Weather table from the content provider */
             val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build()

         /* Used internally as the name of our weather table. */
            const val TABLE_NAME:String = "weather"


            /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load weather data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
            const val COLUMN_DATE:String = "date"
         /* Weather ID as returned by API, used to identify the icon to be used */
            const val COLUMN_WEATHER_ID:String = "weather_id"
         /* Min and max temperatures in °C for the day (stored as floats in the database) */
            const val COLUMN_MIN_TEMP:String = "min"
            const val COLUMN_MAX_TEMP:String = "max"
         /* Humidity is stored as a float representing percentage */
            const val COLUMN_HUMIDITY:String = "humidity"
         /* Pressure is stored as a float representing percentage */
            const val COLUMN_PRESSURE:String = "pressure"
         /* Wind speed is stored as a float representing wind speed in mph */
            const val COLUMN_WIND_SPEED:String = "wind"
         /*
         * Degrees are meteorological degrees (e.g, 0 is north, 180 is south).
         * Stored as floats in the database.
         *
         * Note: These degrees are not to be confused with temperature degrees of the weather.
         */
            const val COLUMN_DEGREES:String = "degrees"

            /* Builds a URI that adds the weather date to the end of the forecast content URI path.
            * This is used to query details about a single weather entry by date. This is what we
            * use for the detail view query. We assume a normalized date is passed to this method.
            *
            * @param date Normalized date in milliseconds
            * @return Uri to query details about a single weather entry
            */
            fun  buildWeatherUriWithDate(date:Long):Uri {
                return CONTENT_URI.buildUpon()
                    .appendPath(date.toString())
                    .build();
            }

            /**
             * Returns just the selection part of the weather query from a normalized today value.
             * This is used to get a weather forecast from today's date. To make this easy to use
             * in compound selection, we embed today's date as an argument in the query.
             *
             * @return The selection part of the weather query for today onwards
             */
            fun getSqlSelectForTodayOnwards(): String {
                val normalizedUtcNow:Long = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
                return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow
            }
        }
    }
}