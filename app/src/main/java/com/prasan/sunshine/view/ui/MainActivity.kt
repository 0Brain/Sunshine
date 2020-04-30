package com.prasan.sunshine.view.ui

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prasan.sunshine.databinding.ActivityMainBinding
import com.prasan.sunshine.utils.NetworkUtils
import com.prasan.sunshine.utils.OpenWeatherJsonUtils
import com.prasan.sunshine.utils.SunshinePreferences
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadWeatherData()

    }

    private fun loadWeatherData() {
        val location: String = SunshinePreferences.getPreferredWeatherLocation(this)
        FetchWeatherTask().execute(location)
    }

    inner class FetchWeatherTask:AsyncTask<String,Unit,Array<String?>>(){
        override fun doInBackground(vararg params: String?): Array<String?> {
            if(params.isEmpty()){
                return null!!
            }
            val preferredLocation: String? = params[0]
            val locationUrl:URL = NetworkUtils.buildUrl(preferredLocation!!)
            try {
                val jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(locationUrl)
                return OpenWeatherJsonUtils
                    .getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse)
            }catch (e:IOException){
                e.printStackTrace()
                return null!!
            }
        }

        override fun onPostExecute(weatherData: Array<String?>) {
            if(weatherData!=null){
                for (weather in weatherData){
                    binding.weatherText.append(weather+"\n\n\n")
                }
            }
        }


    }

}
