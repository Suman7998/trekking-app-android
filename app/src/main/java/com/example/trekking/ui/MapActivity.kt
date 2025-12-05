package com.example.trekking.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trekking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_map)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        // Center camera over Maharashtra
        val maharashtra = LatLng(19.7515, 75.7139)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(maharashtra, 6.5f))

        // Add markers
        trekkingSpots().forEach { (name, latLng) ->
            map.addMarker(MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        }
        waterfalls().forEach { (name, latLng) ->
            map.addMarker(MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        }
        parks().forEach { (name, latLng) ->
            map.addMarker(MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        }
    }

    private fun trekkingSpots(): List<Pair<String, LatLng>> = listOf(
        "Rajmachi Fort, Maharashtra" to LatLng(18.9993, 73.3733),
        "Kalsubai Peak, Maharashtra" to LatLng(19.5999, 73.6999),
        "Harishchandragad, Maharashtra" to LatLng(19.3900, 73.7717),
        "Lohagad Fort, Maharashtra" to LatLng(18.7100, 73.4960),
        "Torna Fort, Maharashtra" to LatLng(18.2783, 73.6736)
    )

    private fun waterfalls(): List<Pair<String, LatLng>> = listOf(
        "Devkund Falls, Maharashtra" to LatLng(18.5395, 73.3366),
        "Thoseghar Falls, Maharashtra" to LatLng(17.5899, 73.8543),
        "Lingmala Falls, Maharashtra" to LatLng(17.9416, 73.6702),
        "Vajrai Falls, Maharashtra" to LatLng(17.5882, 73.7774),
        "Randha Falls, Maharashtra" to LatLng(19.5100, 73.7360)
    )

    private fun parks(): List<Pair<String, LatLng>> = listOf(
        "Sanjay Gandhi NP, Maharashtra" to LatLng(19.2376, 72.9106),
        "Tadoba Andhari TR, Maharashtra" to LatLng(20.2424, 79.3696),
        "Navegaon NP, Maharashtra" to LatLng(20.9702, 80.1469),
        "Chandoli NP, Maharashtra" to LatLng(17.1822, 73.8480),
        "Gugamal NP (Melghat), Maharashtra" to LatLng(21.4595, 77.1905)
    )
}
