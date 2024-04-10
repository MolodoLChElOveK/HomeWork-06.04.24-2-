package com.example.app04063

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var tvSelectedCity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSelectedCity = findViewById(R.id.tvSelectedCity)

        registerForContextMenu(tvSelectedCity)
        tvSelectedCity.setOnClickListener {
            dialogWindow(this)
        }

        findViewById<Button>(R.id.btnGo).setOnClickListener {
            val cityName = tvSelectedCity.text.toString()
            if (cityName.isNotEmpty()) {
                val api = ForecastAPI()

                thread {
                    val text = api.getWeather(cityName)
                    val json = JSONObject(text)

                    if (!json.has("message")) {
                        runOnUiThread {
                            updateWeather(json)
                        }
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Выберите город", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v?.id == R.id.tvSelectedCity) {
            menu?.add(0, v.id, 0, "SELECT_CITY")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tvSelectedCity) {
            dialogWindow(this)
            return true
        }
        return super.onContextItemSelected(item)
    }

        private fun dialogWindow(context: Context) {
        val cities = arrayOf("Октябрьский", "Бавлы", "Уруссу", "Москва", "Лондон")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите город")
            .setItems(cities) { dialog, which ->
                val selectedCity = cities[which]
                tvSelectedCity.text = selectedCity
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun updateWeather(json: JSONObject) {

        val coord = json.getJSONObject("coord")
        val lon = coord.getDouble("lon")
        val lat = coord.getDouble("lat")
        findViewById<TextView>(R.id.tvLonResult).setText(lon.toString())
        findViewById<TextView>(R.id.tvLatResult).setText(lat.toString())

        val weatherArray = json.getJSONArray("weather")
        if (weatherArray.length() > 0) {
            val weather = weatherArray.getJSONObject(0)
            var conditionWeather = weather.getString("description")
            conditionWeather = "${conditionWeather.first() - 32}"+ conditionWeather.substring(1)
            findViewById<TextView>(R.id.tvDescriptionResult).setText(conditionWeather)
        }

        val main = json.getJSONObject("main")
        findViewById<TextView>(R.id.tvTempResult1).setText(main.getDouble("temp").roundToInt().toString())
        findViewById<TextView>(R.id.tvTempResult2).setText(main.getDouble("feels_like").roundToInt().toString())

        val sys = json.getJSONObject("sys")
        val sunrise = Date(sys.getLong("sunrise") * 1000)
        val sunset = Date(sys.getLong("sunset") * 1000)
        findViewById<TextView>(R.id.tvSunriseResult).setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(sunrise))
        findViewById<TextView>(R.id.tvSunsetResult).setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(sunset))
    }

}
