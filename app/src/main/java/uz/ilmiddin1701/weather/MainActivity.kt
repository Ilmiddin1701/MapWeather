package uz.ilmiddin1701.weather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import uz.ilmiddin1701.weather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var mMap: GoogleMap
    var lat: Double = 0.0
    var lon: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMapClickListener {
            mMap.clear()
            lat = it.latitude
            lon = it.longitude
            mMap.addMarker(MarkerOptions().position(it))
        }
        mMap.setOnMarkerClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            startActivity(intent)
            true
        }
    }
}