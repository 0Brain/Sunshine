package com.prasan.sunshine.view.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import com.prasan.sunshine.R

class MySettingsFragment : PreferenceFragmentCompat(),SharedPreferences.OnSharedPreferenceChangeListener{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_prefs,rootKey)


        val sharedPreferences = preferenceScreen.sharedPreferences
        val preferenceScreenInstance = preferenceScreen
        val count = preferenceScreenInstance.preferenceCount

        for(i in 0 until count){
            val pref = preferenceScreenInstance.getPreference(i)
            if(pref !is CheckBoxPreference){
                val value = sharedPreferences.getString(pref.key,"")
                setPreferenceSummary(pref,value)
            }
        }

    }

    override fun onStart() {
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        super.onStart()
    }

    override fun onStop() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    private fun setPreferenceSummary(pref: Preference?, value: String?) {
        if(pref is ListPreference){
            val index = pref.findIndexOfValue(value)
            if(index>=0){
                pref.summary = pref.entries[index]
            }
        }else if(pref is EditTextPreference){
            pref.summary = value
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference:Preference? = findPreference(key!!)
        if(preference!=null){
            if(preference !is CheckBoxPreference){
                setPreferenceSummary(preference,sharedPreferences!!.getString(preference.key,""))
            }
        }
    }

}