package edu.msudenver.cs3013.lab04

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    private val _savedLocation = MutableLiveData<LatLng>()
    val savedLocation: LiveData<LatLng> = _savedLocation

    fun setLocation(location: LatLng) {
        _savedLocation.value = location
    }
}