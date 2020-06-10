package com.prasan.sunshine.sync

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.provider.BaseColumns
import com.prasan.sunshine.data.WeatherContract


class SunshineSyncUtils {
    companion object {
        private var sInitialized = false
        private lateinit var mBackgroundTask: AsyncTask<Unit, Unit, Unit>
        @Synchronized
        fun initialize(context: Context?) {
            if(sInitialized) return
            sInitialized = true

            @SuppressLint("StaticFieldLeak")
            mBackgroundTask = object : AsyncTask<Unit, Unit, Unit>() {
                override fun doInBackground(vararg params: Unit?) {
                    /*
                    * Since this query is going to be used only as a check to see if we have any
                    * data (rather than to display data), we just need to PROJECT the ID of each
                    * row. In our queries where we display data, we need to PROJECT more columns
                    * to determine what weather details need to be displayed.
                    */
                    val weatherUri: Uri = WeatherContract.WeatherEntry.CONTENT_URI
                    val projectionColumns = arrayOf(BaseColumns._ID)
                    val selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards()

                    val cursor = context!!.contentResolver.query(weatherUri,
                    projectionColumns,
                        selectionStatement,
                        null,
                        null)
                    if(cursor == null || cursor.count == 0){
                        startImmediately(context)
                    }
                    cursor!!.close()
                }

            }
            mBackgroundTask.execute()
        }


        fun startImmediately(context: Context) {
            val intentToSyncImmediately = Intent(context, SunshineSyncIntentService::class.java)
            context.startService(intentToSyncImmediately)
        }
    }
}