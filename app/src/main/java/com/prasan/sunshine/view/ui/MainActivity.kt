package com.prasan.sunshine.view.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prasan.sunshine.R
import com.prasan.sunshine.databinding.ActivityMainBinding
import com.prasan.sunshine.utils.NetworkUtils
import com.prasan.sunshine.utils.OpenWeatherJsonUtils
import com.prasan.sunshine.data.SunshinePreferences
import com.prasan.sunshine.view.adapters.SunshineAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL


class MainActivity : AppCompatActivity(),SunshineAdapter.SunshineAdapterOnClickHandler,
    LoaderManager.LoaderCallbacks<Array<String?>>,SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding
    private lateinit var sunshineAdapter:SunshineAdapter
    private lateinit var weatherRecyclerView: RecyclerView

    companion object{
        private const val FORECAST_LOADER_ID = 0
        private var PREFERENCES_HAVE_BEEN_UPDATED = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sunshineAdapter = SunshineAdapter(this)
        weatherRecyclerView = findViewById(R.id.rv_weather)
        rv_weather.layoutManager = LinearLayoutManager(this)
        rv_weather.setHasFixedSize(true)
        rv_weather.adapter = sunshineAdapter
        loadWeatherData()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.forecast_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_refresh -> {
                sunshineAdapter.weatherData = emptyArray()
                supportLoaderManager.restartLoader(FORECAST_LOADER_ID,null,this@MainActivity)
            }
            R.id.action_map ->{
                openLocationMap()
            }
            R.id.settings->{
                val intent = Intent(this@MainActivity,SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLocationMap() {
        val addressString = SunshinePreferences.getPreferredWeatherLocation(this)
        val geoLocation: Uri = Uri.parse("geo:0,0?q=$addressString")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if(intent.resolveActivity(packageManager)!=null){
            startActivity(intent)
        }else{
            Log.d(TAG, "Couldn't call $geoLocation, no receiving apps installed!");
        }

    }


    private fun loadWeatherData() {
        supportLoaderManager.initLoader(FORECAST_LOADER_ID,null,this@MainActivity)
    }

    override fun onStart() {
        if(PREFERENCES_HAVE_BEEN_UPDATED){
            supportLoaderManager.restartLoader(FORECAST_LOADER_ID,null,this@MainActivity)
            PREFERENCES_HAVE_BEEN_UPDATED = false
        }
        super.onStart()
    }

    override fun onClick(weatherForTheDay: String?) {
        Log.d("test","clicked")
        val intent = Intent(this,DetailActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT,weatherForTheDay)
        startActivity(intent)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Array<String?>> {
        return object :AsyncTaskLoader<Array<String?>>(this){

            var mWeatherData: Array<String?>? = null

            override fun onStartLoading() {
                if(mWeatherData!=null){
                    deliverResult(mWeatherData)
                }else{
                    binding.pbLoadingIndicator.visibility = View.VISIBLE
                    forceLoad()
                }
            }

            override fun loadInBackground(): Array<String?>? {
                val weatherRequestUrl:URL = NetworkUtils.getUrl(context)
                var simpleJsonWeatherData:Array<String?>? = null
                try {
                    val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)
                    simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse)

                }catch (e:Exception){
                    e.printStackTrace()
                }
                return simpleJsonWeatherData
            }

            override fun deliverResult(weatherData: Array<String?>?) {
                mWeatherData = weatherData
                super.deliverResult(weatherData)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Array<String?>>, arrayWeatherData: Array<String?>) {
        sunshineAdapter.weatherData = arrayWeatherData
        binding.pbLoadingIndicator.visibility = View.INVISIBLE
    }

    override fun onLoaderReset(loader: Loader<Array<String?>>) {
        TODO("Not yet implemented")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        PREFERENCES_HAVE_BEEN_UPDATED = true
    }


}
