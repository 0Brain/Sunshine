package com.prasan.sunshine.view.ui

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.prasan.sunshine.R
import com.prasan.sunshine.data.SunshinePreferences
import com.prasan.sunshine.data.WeatherContract
import com.prasan.sunshine.databinding.ActivityMainBinding
import com.prasan.sunshine.utils.FakeDataUtils
import com.prasan.sunshine.view.adapters.SunshineAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),SunshineAdapter.SunshineAdapterOnClickHandler,
    LoaderManager.LoaderCallbacks<Cursor>,SharedPreferences.OnSharedPreferenceChangeListener {



    private lateinit var binding: ActivityMainBinding
    private lateinit var sunshineAdapter:SunshineAdapter
    private lateinit var weatherRecyclerView: RecyclerView

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val FORECAST_LOADER_ID = 45
        private var PREFERENCES_HAVE_BEEN_UPDATED = false
        const val INDEX_WEATHER_DATE = 0
        const val INDEX_WEATHER_MAX_TEMP = 1
        const val INDEX_WEATHER_MIN_TEMP = 2
        const val INDEX_WEATHER_CONDITION_ID = 3
        private var mPosition = RecyclerView.NO_POSITION
        val MAIN_FORECAST_PROJECTION = arrayOf(
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Stetho.initializeWithDefaults(this)
        FakeDataUtils.insertFakeData(this)

        sunshineAdapter = SunshineAdapter(this,this)
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
        binding.pbLoadingIndicator.visibility = View.VISIBLE
        binding.rvWeather.visibility = View.INVISIBLE
        supportLoaderManager.initLoader(FORECAST_LOADER_ID,null,this@MainActivity)
    }

    override fun onStart() {
        if(PREFERENCES_HAVE_BEEN_UPDATED){
            supportLoaderManager.restartLoader(FORECAST_LOADER_ID,null,this@MainActivity)
            PREFERENCES_HAVE_BEEN_UPDATED = false
        }
        super.onStart()
    }

    override fun onClick(date: Long?) {
        Log.d("test","clicked")
        val intent = Intent(this,DetailActivity::class.java)
        val uriForDataClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date!!)
        intent.data = uriForDataClicked
        startActivity(intent)
    }



    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        PREFERENCES_HAVE_BEEN_UPDATED = true
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        when(loaderId){
            FORECAST_LOADER_ID ->{
                /* URI for all rows of weather data in our weather table */
                val sunshineUri = WeatherContract.WeatherEntry.CONTENT_URI
                /* Sort order: Ascending by date */
                val sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                val selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards()

                return CursorLoader(
                    this@MainActivity,
                    sunshineUri,
                    MAIN_FORECAST_PROJECTION,
                    selection,
                    null,
                    sortOrder
                )
            }else->{
                throw IllegalArgumentException("Loader not implemented $loaderId")
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        sunshineAdapter.swapCursor(data)
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        weatherRecyclerView.scrollToPosition(mPosition)
        if(data!!.count!=0){
            binding.pbLoadingIndicator.visibility = View.INVISIBLE;
            /* Finally, make sure the weather data is visible */
            weatherRecyclerView.visibility = View.VISIBLE;
        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        /*
        * Since this Loader's data is now invalid, we need to clear the Adapter that is
        * displaying the data.
        */
        sunshineAdapter.swapCursor(null);
    }
}
