package edu.msudenver.cs3013.lab04

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var parkedMarker: Marker? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val button: Button = findViewById(R.id.btn_parked_here)
        button.setOnClickListener {
            onParkedHere()
        }

        checkLocationPermission()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getUserLocation()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously*
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, yay! Do the location-related task you need to do.
                    getUserLocation()
                } else {
                    // Permission denied, boo! Disable the functionality that depends on this permission.
                }
                return
            }
            // Other 'when' lines to check for other permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling ActivityCompat#requestPermissions here to request the missing permissions
            return
        }
        mMap.isMyLocationEnabled = true
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    addMarkerAtLocation(currentLocation, "Current Location")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
    }

    private fun addMarkerAtLocation(location: LatLng, title: String) {
        if (parkedMarker != null) {
            parkedMarker!!.remove()
        }
        parkedMarker = mMap.addMarker(MarkerOptions().position(location).title(title))
    }

    private fun onParkedHere() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling ActivityCompat#requestPermissions here to request the missing permissions
            return
        }
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val parkedLocation = LatLng(location.latitude, location.longitude)
                    addMarkerAtLocation(parkedLocation, "Parked Here")
                    // Save this location to the ViewModel (to be implemented in the next steps)
                }
            }
    }
}