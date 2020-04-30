package com.prasan.sunshine.utils

object AppConstants{
     const val DYNAMIC_WEATHER_URL = "https://andfun-weather.udacity.com/weather"

     const val STATIC_WEATHER_URL =
        "https://andfun-weather.udacity.com/staticweather"

     const val FORECAST_BASE_URL = STATIC_WEATHER_URL

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */
    /* The format we want our API to return */
     const val format = "json"

    /* The units we want our API to return */
     const val units = "metric"

    /* The number of days we want our API to return */
     const val numDays = 14

    const val QUERY_PARAM = "q"
    const val LAT_PARAM = "lat"
    const val LON_PARAM = "lon"
    const val FORMAT_PARAM = "mode"
    const val UNITS_PARAM = "units"
    const val DAYS_PARAM = "cnt"
}