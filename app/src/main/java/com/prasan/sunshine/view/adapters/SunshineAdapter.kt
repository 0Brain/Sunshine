package com.prasan.sunshine.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prasan.sunshine.R

class SunshineAdapter : RecyclerView.Adapter<SunshineAdapter.SunShineViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SunShineViewHolder {
        val greenView =
            LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return SunShineViewHolder(greenView)
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }


    override fun onBindViewHolder(holder: SunShineViewHolder, position: Int) {
        holder.onBind(weatherData[position])
    }

    var weatherData: Array<String?> = arrayOf()
        set(mWeatherData) {
            field = mWeatherData
            notifyDataSetChanged()
        }

    inner class SunShineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(weatherForTheDay: String?) {
            val weatherTextView = itemView.findViewById(R.id.tv_weather_text) as TextView
            weatherTextView.text = weatherForTheDay.toString()
        }

    }
}