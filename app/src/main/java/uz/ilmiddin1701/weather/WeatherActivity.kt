package uz.ilmiddin1701.weather

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.ilmiddin1701.weather.databinding.ActivityWeatherBinding
import uz.ilmiddin1701.weather.modles.WeatherResponse
import uz.ilmiddin1701.weather.retrofit.ApiClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    private val binding by lazy { ActivityWeatherBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Weather API key `536d75f767d8011b59b923194bcd0f3e`

        val latitude = intent.getDoubleExtra("lat", 0.0)
        val longitude = intent.getDoubleExtra("lon", 0.0)

//        getWeatherInfo(latitude, longitude)
        getWeatherInfo(latitude, longitude)
    }

    private fun getWeatherInfo(latitude: Double, longitude: Double) {
        val apiKey = "536d75f767d8011b59b923194bcd0f3e"
        ApiClient.api.getWeather(latitude, longitude, apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()

                        weatherResponse?.let {
                            val temp = it.main.temp
                            binding.tvDateTime.text = unixTimeToDate(it.dt.toLong())
                            binding.tvTemperature.text = " ${temp.toInt()}°"
                            val geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())
                            try {
                                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                                if (addresses!!.isNotEmpty()) {
                                    val address = addresses[0]
                                    val cityName = address.locality
                                    val countryName = address.countryName
                                    binding.apply {
                                        if (!cityName.isNullOrBlank() && !countryName.isNullOrBlank()){
                                            tvLocation.text = "$countryName: $cityName"
                                            tvLocation.visibility = View.VISIBLE
                                        } else if (!cityName.isNullOrBlank() && countryName.isNullOrBlank()) {
                                            tvLocation.text = cityName
                                            tvLocation.visibility = View.VISIBLE
                                        } else if (cityName.isNullOrBlank() && !countryName.isNullOrBlank()) {
                                            tvLocation.text = countryName
                                            tvLocation.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            binding.tvDescription.text = it.weather[0].description.capitalizeWords()
                            binding.apply {
                                when (tvDescription.text) {
                                    "Clear Sky" -> {
                                        imgDescription.setImageResource(R.drawable.clear_sky)
                                        startRotatingAnimation()
                                    }
                                    "Overcast Clouds" -> imgDescription.setImageResource(R.drawable.overcast_clouds)
                                    "Broken Clouds" -> imgDescription.setImageResource(R.drawable.broken_clouds)
                                    "Few Clouds" -> imgDescription.setImageResource(R.drawable.few_clouds)
                                    "Scattered Clouds" -> imgDescription.setImageResource(R.drawable.scattered_clouds)
                                    "Light Rain" -> imgDescription.setImageResource(R.drawable.light_rain)
                                    "Haze" -> imgDescription.setImageResource(R.drawable.haze)
                                    "Mist" -> imgDescription.setImageResource(R.drawable.mist)
                                    "Moderate Rain" -> Picasso.get().load("https://openweathermap.org/img/wn/${weatherResponse.weather[0].icon}@2x.png").into(imgDescription)
                                }
                            }
                            binding.tvRain.text = if (it.rain != null) { "${it.rain.`1h` ?: "N/A"} mm" } else { "No" }
                            binding.tvWind.text = "${it.wind.speed} km/h"
                            binding.tvHumidity.text = "${it.main.humidity} %"
                            binding.constraintLayout.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@WeatherActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
//                    textView.text = "That didn't work! ${t.message}"
                }
            })
    }
    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

    private fun unixTimeToDate(unixTime: Long): String {
        val date = Date(unixTime * 1000)
        val sdf = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault())
        return "Current Date and Time: " + sdf.format(date)
    }

    private fun startRotatingAnimation() {
        val rotateAnimator = ObjectAnimator.ofFloat(binding.imgDescription, "rotation", 0f, 360f)
        rotateAnimator.duration = 18000 // 5 soniya davomida aylanish
        rotateAnimator.repeatCount = ObjectAnimator.INFINITE // Cheksiz qayta takrorlash
        rotateAnimator.start()
    }
}