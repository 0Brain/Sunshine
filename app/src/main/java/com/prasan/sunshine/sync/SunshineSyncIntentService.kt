package com.prasan.sunshine.sync

import android.app.IntentService
import android.content.Intent

class SunshineSyncIntentService : IntentService("SunshineSyncIntentService"){
    override fun onHandleIntent(intent: Intent?) {
       SunshineSyncTask.syncWeather(this)
    }
}