package com.prasan.sunshine.data

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.prasan.sunshine.utils.SunshineDateUtils


class WeatherProvider : ContentProvider() {

    companion object {
        private const val CODE_WEATHER = 100
        private const val CODE_WEATHER_WITH_DATE = 101

        private val sUriMatcher: UriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {

            /*
            * All paths added to the UriMatcher have a corresponding code to return when a match is
            * found. The code passed into the constructor of UriMatcher here represents the code to
            * return for the root URI. It's common to use NO_MATCH as the code for this case.
            */
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority: String = WeatherContract.CONTENT_AUTHORITY

            /*
             * For each type of URI you want to add, create a corresponding code. Preferably, these are
             * constant fields in your class so that you can use them throughout the class and you no
             * they aren't going to change. In Sunshine, we use CODE_WEATHER or CODE_WEATHER_WITH_DATE.
             */
            /* This URI is content://com.example.android.sunshine/weather/ */
            uriMatcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER)

            /*
             * This URI would look something like content://com.example.android.sunshine/weather/1472214172
             * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
             * that it should return the CODE_WEATHER_WITH_DATE code
             */
            uriMatcher.addURI(
                authority,
                WeatherContract.PATH_WEATHER + "/#",
                CODE_WEATHER_WITH_DATE
            )


            return uriMatcher
        }
    }

    private lateinit var openHelper: WeatherDbHelper

    override fun onCreate(): Boolean {
        openHelper = WeatherDbHelper(context!!)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    @SuppressLint("Recycle")
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        var cursor: Cursor? = null
        when (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.android.sunshine/weather/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
            CODE_WEATHER -> {
                cursor = openHelper.readableDatabase.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }

            CODE_WEATHER_WITH_DATE -> {
                /*
                 * In order to determine the date associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 1472214172 and
                 * represents the number of seconds since the epoch, or UTC time.
                 */
                val dataString: String? = uri.lastPathSegment

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                val selectionArguments = arrayOf(dataString!!)

                cursor = openHelper.readableDatabase.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    /*
                     * A projection designates the columns we want returned in our Cursor.
                     * Passing null will return all columns of data within the Cursor.
                     * However, if you don't need all the data from the table, it's best
                     * practice to limit the columns returned in the Cursor with a projection.
                     */
                    projection,
                    /*
                     * The URI that matches CODE_WEATHER_WITH_DATE contains a date at the end
                     * of it. We extract that date and use it with these next two lines to
                     * specify the row of weather we want returned in the cursor. We use a
                     * question mark here and then designate selectionArguments as the next
                     * argument for performance reasons. Whatever Strings are contained
                     * within the selectionArguments array will be inserted into the
                     * selection statement by SQLite under the hood.
                     */
                    WeatherContract.WeatherEntry.COLUMN_DATE + "=?",
                    selectionArguments,
                    null,
                    null,
                    sortOrder
                )
            }
            else ->
                throw UnsupportedOperationException("Unknown uri: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri);
        return cursor
    }

    /**
     * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
     * inserting multiple rows of data at a time from a weather forecast. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        val db: SQLiteDatabase = openHelper.writableDatabase

        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                db.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {
                        val normalizedData =
                            value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)
                        if (!SunshineDateUtils.isDateNormalized(normalizedData)) {
                            throw IllegalArgumentException("Date must be normalized")
                        }
                        val id: Long =
                            db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value)
                        if (id != -1L) {
                            rowsInserted++
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction()
                }
                return rowsInserted
            }
            else -> return super.bulkInsert(uri, values)
        }
    }


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db: SQLiteDatabase = openHelper.writableDatabase
        var rowsUpdated = 0
        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                val normalizedDate = values!!.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)
                if (!SunshineDateUtils.isDateNormalized(normalizedDate)) throw IllegalArgumentException(
                    "Date is not normalized"
                )
                rowsUpdated = db.update(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid URI")
            }
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db: SQLiteDatabase = openHelper.writableDatabase
        val rowsDeleted: Int
        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                rowsDeleted =
                    db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> {
                throw IllegalArgumentException("Uri $uri not found")
            }
        }
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        openHelper.close()
        super.shutdown()
    }
}