package edu.msudenver.cs3013.lab04

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import edu.msudenver.cs3013.lab04.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private var parkedMarker: Marker? = null
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getLastLocation()
                } else {
                    // if should show rationale, show it
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showPermissionRationale {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            }

        binding.btnParkedHere.setOnClickListener {
            onParkedHere()
        }
    }

    private fun hasLocationPermission() =
        //check if ACCESS_FINE_LOCATION permission is granted
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        when {
            hasLocationPermission() -> getLastLocation()
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showPermissionRationale(
        positiveAction: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Location permission")
            .setMessage("We need your permission to find your current position")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLocation = LatLng(it.latitude, it.longitude)
                        updateMapLocation(currentLocation)
                        addMarkerAtLocation(currentLocation, "Current Location")
                        //zoom in
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    }
                }
        }
    }

    private fun updateMapLocation(location: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 7f))
    }

    private fun addMarkerAtLocation(location: LatLng, title: String) {
        if (parkedMarker != null) {
            parkedMarker!!.remove()
        }
        parkedMarker = mMap.addMarker(MarkerOptions().position(location).title(title))
    }

    private fun onParkedHere() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val parkedLocation = LatLng(it.latitude, it.longitude)
                        addMarkerAtLocation(parkedLocation, "Parked Here")
                        locationViewModel.setLocation(parkedLocation)
                    }
                }
        }
    }
}
