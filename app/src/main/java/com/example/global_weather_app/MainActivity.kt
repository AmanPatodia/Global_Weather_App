package com.example.global_weather_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.global_weather_app.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("delhi")
        SearchName()
    }

    private fun SearchName() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        val response =
            retrofit.getWetherData(cityName, "ba01fc124d331fba9cae425afce576a8", "metric")

        response.enqueue(object : Callback<weatherApp> {
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    var temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val seaLevel = responseBody.main.pressure
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    var maxTemp = responseBody.main.temp_max.toString()
                    var minTemp = responseBody.main.temp_min.toString()
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Min Temp : $minTemp °C"
                    binding.Humidity.text = "$humidity %"
                    binding.Windspeed.text = "$windSpeed m/s"
                    binding.Sunrise.text = "${time(sunRise)}"
                    binding.SunSet.text = "${time(sunSet)}"
                    binding.Sea.text = "$seaLevel hPa"
                    binding.Condition.text = condition
                    binding.date.text = date()
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.cityName.text = "$cityName"

                    changeBackgrounds(condition)
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeBackgrounds(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunday)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            " Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy","Smoke" -> {
                binding.root.setBackgroundResource(R.drawable.cloudy)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rainday)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunday)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}