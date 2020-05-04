package com.prasan.sunshine.view.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prasan.sunshine.R
import com.prasan.sunshine.databinding.ActivityMainBinding
import com.prasan.sunshine.utils.NetworkUtils
import com.prasan.sunshine.utils.OpenWeatherJsonUtils
import com.prasan.sunshine.utils.SunshinePreferences
import com.prasan.sunshine.view.adapters.SunshineAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sunshineAdapter:SunshineAdapter
    private lateinit var weatherRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sunshineAdapter = SunshineAdapter()
        weatherRecyclerView = findViewById(R.id.rv_weather)
        rv_weather.layoutManager = LinearLayoutManager(this)
        rv_weather.setHasFixedSize(true)
        rv_weather.adapter = sunshineAdapter
        loadWeatherData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.forecast_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_refresh -> {
                binding.rvWeather.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
                loadWeatherData()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun loadWeatherData() {
        val location: String? = SunshinePreferences.getPreferredWeatherLocation(this)
        FetchWeatherTask().execute(location)
    }

    inner class FetchWeatherTask:AsyncTask<String,Unit,Array<String?>?>(){
        override fun doInBackground(vararg params: String?): Array<String?>? {
            if(params.isEmpty()){
                return null!!
            }
            val preferredLocation: String? = params[0]
            val locationUrl:URL
            var simpleJsonWeatherData:Array<String?>? = null
            try {
                locationUrl = NetworkUtils.buildUrl(preferredLocation!!)
                val jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(locationUrl)
                simpleJsonWeatherData = OpenWeatherJsonUtils
                    .getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse)
            }catch (e:IOException){
                e.printStackTrace()
            }
            return simpleJsonWeatherData
        }

        override fun onPostExecute(weatherData: Array<String?>?) {
            binding.tvErrorText.text = getString(R.string.load_error_message)
            binding.pbLoadingIndicator.visibility = View.INVISIBLE
            if(weatherData!=null){
                binding.tvErrorText.visibility = View.INVISIBLE
                binding.rvWeather.visibility = View.VISIBLE
                sunshineAdapter.WeatherData(weatherData)
            }else{
                binding.tvErrorText.visibility = View.VISIBLE
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            binding.pbLoadingIndicator.visibility = View.VISIBLE
        }


    }

}
