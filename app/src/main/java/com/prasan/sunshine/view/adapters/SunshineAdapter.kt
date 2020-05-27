package com.prasan.sunshine.view.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prasan.sunshine.R
import com.prasan.sunshine.utils.SunshineDateUtils
import com.prasan.sunshine.utils.SunshineWeatherUtils
import com.prasan.sunshine.view.ui.MainActivity


class SunshineAdapter(context:Context,clickHandler: SunshineAdapterOnClickHandler) :
    RecyclerView.Adapter<SunshineAdapter.SunShineViewHolder>() {

    var weatherData: Array<String?> = arrayOf()
        set(mWeatherData) {
            field = mWeatherData
            notifyDataSetChanged()
        }
    /* The context we use to utility methods, app resources and layout inflaters */
    private var mContext: Context? = null
    private var mCursor:Cursor? = null
    var mClickHandler:SunshineAdapterOnClickHandler? = clickHandler
    init {
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SunShineViewHolder {
        val greenView =
            LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return SunShineViewHolder(greenView)
    }

    override fun getItemCount(): Int {
        if(mCursor == null ) return 0
        return  mCursor!!.count
    }

    fun swapCursor(newCursor:Cursor?){
        mCursor = newCursor!!
        notifyDataSetChanged()
    }
    interface SunshineAdapterOnClickHandler{
        fun onClick(date:Long?)
    }

    override fun onBindViewHolder(holder: SunShineViewHolder, position: Int) {
        mCursor!!.moveToPosition(position)
        /* Read date from the cursor */
        val dateInMillis = mCursor!!.getLong(MainActivity.INDEX_WEATHER_DATE)
        /* Get human readable string using our utility method */
        val dateString = SunshineDateUtils.getFriendlyDateString(mContext!!, dateInMillis, false)
        /* Use the weatherId to obtain the proper description */
        val weatherId = mCursor!!.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID)
        val description = SunshineWeatherUtils.getStringForWeatherCondition(mContext!!, weatherId)
        /* Read high temperature from the cursor (in degrees celsius) */
        val highInCelsius = mCursor!!.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP)
        /* Read low temperature from the cursor (in degrees celsius) */
        val lowInCelsius = mCursor!!.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP)

        val highAndLowTemperature = SunshineWeatherUtils.formatHighLows(mContext!!, highInCelsius, lowInCelsius)

        val weatherSummary = "$dateString - $description - $highAndLowTemperature"
        holder.weatherTextView.text = weatherSummary
    }

    inner class SunShineViewHolder(view:View) : RecyclerView.ViewHolder(view) ,View.OnClickListener{
        var weatherTextView:TextView
        init {
            view.setOnClickListener(this)
            weatherTextView = itemView.findViewById(R.id.tv_weather_text) as TextView
        }

        override fun onClick(v: View?) {
            val dateInMillis = mCursor!!.getLong(MainActivity.INDEX_WEATHER_DATE)
            mClickHandler!!.onClick(dateInMillis)
        }

    }
}