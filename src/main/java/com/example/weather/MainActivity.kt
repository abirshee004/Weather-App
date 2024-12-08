package com.example.weather

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition
import kotlin.math.log

//78ed425a7371c5e963296ee67d4f7272

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Kolkata")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchview1
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
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

    private fun fetchWeatherData(cityName:String) {
         val retrofit = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("https://api.openweathermap.org/data/2.5/")
             .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "78ed425a7371c5e963296ee67d4f7272", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(p0: Call<WeatherApp>, p1: Response<WeatherApp>) {
                val responseBody = p1.body()
                if(response.isExecuted && responseBody != null){
                    var temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.humadity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed M/s"
                    binding.sunrise.text = "${formatTime(sunRise)}"
                    binding.sunset.text = "${formatTime(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.dateMonthYear.text = date()
                        binding.city.text ="$cityName"
                    //Log.d("TAG", "onResponse: $temperature")

                    changeImagesAccordingToWeathercondition(condition)

                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImagesAccordingToWeathercondition(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear", "Mostly Sunny", "Bright" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Cloudy", "Cloudy", "Overcast", "Mist", "Foggy", "Hazy", "Smoky","Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain", "Thunderstorm", "Torrential Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Snow", "Snow Showers", "Flurries", "Sleet" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Thunderstorm", "Lightning", "Severe Storm", "Windstorm" -> {
                binding.root.setBackgroundResource(R.drawable.strom_background1)
                binding.lottieAnimationView.setAnimation(R.raw.strom)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String? {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}

