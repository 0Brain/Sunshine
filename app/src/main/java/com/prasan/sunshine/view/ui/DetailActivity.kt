package com.prasan.sunshine.view.ui

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.prasan.sunshine.R
import com.prasan.sunshine.data.WeatherContract
import com.prasan.sunshine.databinding.ActivityDetailBinding
import com.prasan.sunshine.utils.SunshineDateUtils
import com.prasan.sunshine.utils.SunshineWeatherUtils


class DetailActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor?> {


    private lateinit var binding: ActivityDetailBinding
    private var weatherStringText: String? = null
    private lateinit var mURI: Uri
    private var mForecastSummary: String? = null

    companion object {
        const val DETAILS_ID = 22
        const val INDEX_WEATHER_CONDITION_ID = 0
        const val INDEX_WEATHER_DATE = 1
        const val INDEX_WEATHER_MAX_TEMP = 2
        const val INDEX_WEATHER_MIN_TEMP = 3
        const val INDEX_WEATHER_HUMIDITY = 4
        const val INDEX_WEATHER_PRESSURE = 5
        const val INDEX_WEATHER_WIND_SPEED = 6
        const val INDEX_WEATHER_DEGREES = 7

        val FORECAST_PROJECTION = arrayOf(
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES

        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mURI = intent.data!!
        supportLoaderManager.initLoader(DETAILS_ID,null,this@DetailActivity)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                shareCompatBuilder(weatherStringText)
            }
            R.id.settings -> {
                val intent = Intent(this@DetailActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    private fun shareCompatBuilder(weatherString: String?) {
        val contentType = "text/plain"
        val title = "Share Weather"

        ShareCompat.IntentBuilder.from(this)
            .setType(contentType)
            .setChooserTitle(title)
            .setText(weatherString)
            .startChooser()
    }

    /**
     * Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param loaderArgs Any arguments supplied by the caller
     *
     * @return A new Loader instance that is ready to start loading.
     */

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor?> {
        when (loaderId) {
            DETAILS_ID -> {
                return CursorLoader(
                    this,
                    mURI,
                    FORECAST_PROJECTION,
                    null,
                    null,
                    null
                )
            }
            else -> {
                throw IllegalArgumentException("loader not implemented $loaderId")
            }
        }
    }

    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the details of the weather on the date they
     * selected from the forecast.
     *
     * @param loader The cursor loader that finished.
     * @param data   The cursor that is being returned.
     */

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        var cursorHasValidData = false
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true
        }
        if (!cursorHasValidData) {
            return
        }

        /****************
         * Weather Date *
         ****************/
        /*
         * Read the date from the cursor. It is important to note that the date from the cursor
         * is the same date from the weather SQL table. The date that is stored is a GMT
         * representation at midnight of the date when the weather information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */

        val localDateMidnightGmt: Long = data!!.getLong(INDEX_WEATHER_DATE)
        val dateText = SunshineDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true)

        binding.tvDate.text = dateText

        /***********************
         * Weather Description *
         ***********************/
        /* Read weather condition ID from the cursor (ID provided by Open Weather Map) */
        val weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID)
        /* Use the weatherId to obtain the proper description */
        val description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId)

        /* Set the text */
        binding.tvDescription.text = description

        /**************************
         * High (max) temperature *
         **************************/
        /* Read high temperature from the cursor (in degrees celsius) */
        val highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius)

        /* Set the text */
        binding.tvMaxTemperature.text = highString

        /*************************
         * Low (min) temperature *
         *************************/
        /* Read low temperature from the cursor (in degrees celsius) */
        val lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius)

        /* Set the text */
        binding.tvMinTemperature.text = lowString

        /************
         * Humidity *
         ************/
        /* Read humidity from the cursor */
        val humidity = data.getFloat(INDEX_WEATHER_HUMIDITY)
        val humidityString = getString(R.string.format_humidity, humidity)

        /* Set the text */
        binding.tvHumidity.text = humidityString

//      COMPLETED (31) Display the wind speed and direction
        /****************************
         * Wind speed and direction *
         ****************************/
        /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
        val windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED)
        val windDirection = data.getFloat(INDEX_WEATHER_DEGREES)
        val windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection)

        /* Set the text */
        binding.tvWind.text = windString

        /************
         * Pressure *
         ************/
        /* Read pressure from the cursor */
        val pressure = data.getFloat(INDEX_WEATHER_PRESSURE)

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        val pressureString = getString(R.string.format_pressure, pressure)

        /* Set the text */
        binding.tvPressure.text = pressureString

        /* Store the forecast summary String in our forecast summary field to share later */
        mForecastSummary = String.format("%s - %s - %s/%s",dateText, description, highString, lowString)
    }
    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor?>) {
        TODO("Not yet implemented")
    }

}
