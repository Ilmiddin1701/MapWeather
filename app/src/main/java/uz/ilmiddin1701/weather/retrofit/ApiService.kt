package uz.ilmiddin1701.weather.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uz.ilmiddin1701.weather.modles.WeatherResponse

interface ApiService {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>
}