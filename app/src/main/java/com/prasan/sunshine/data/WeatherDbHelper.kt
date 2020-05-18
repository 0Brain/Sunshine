package com.prasan.sunshine.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class WeatherDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {

    companion object{
     /*
    * This is the name of our database. Database names should be descriptive and end with the
    * .db extension.
    */
        const val DATABASE_NAME:String = "weather.db"
        /*
        * If you change the database schema, you must increment the database version or the onUpgrade
        * method will not be called.
        *
        * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
        * with the Android course for a while now. Believe it or not, older versions of Sunshine
        * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
        * versions of Sunshine could cause everything to break. Although that is certainly a rare
        * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
        * version your databases.
        */
        const val DATABASE_VERSION:Int = 1
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        val SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
        /*
         * WeatherEntry did not explicitly declare a column called "_ID". However,
         * WeatherEntry implements the interface, "BaseColumns", which does have a field
         * named "_ID". We use that here to designate our table's primary key.
         */

                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +

                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + "REAL NOT NULL" +

                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + "REAL NOT NULL" +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + "REAL NOT NULL" +

                WeatherContract.WeatherEntry.COLUMN_PRESSURE + "REAL NOT NULL" +

                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + "REAL NOT NULL" +

                WeatherContract.WeatherEntry.COLUMN_DEGREES + "REAL NOT NULL" +
                "); "
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db!!.execSQL(SQL_CREATE_WEATHER_TABLE)
    }


    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS"+WeatherContract.WeatherEntry.TABLE_NAME)
        onCreate(db)
    }
}