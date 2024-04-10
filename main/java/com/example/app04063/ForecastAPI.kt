package com.example.app04063

import java.net.HttpURLConnection
import java.net.URL

public class ForecastAPI {
    val baseURl = "https://api.openweathermap.org/data/2.5/weather?"

    public fun getWeather(city: String): String {
        var cityName = city
        if (cityName == "Октябрьский") cityName = "Октябрьский, Башкортостан, Россия"
        val appid = "4b39e3cf4e3e676c465e0c5e45429234"
        val url = "${baseURl}q=$cityName&units=metric&appid=$appid&lang=ru"
        return getURLDate(url)
    }

    public fun getURLDate(path: String): String {
        val connection = URL(path).openConnection() as HttpURLConnection
        val data = connection.inputStream.bufferedReader().readText()
        return data
    }
}