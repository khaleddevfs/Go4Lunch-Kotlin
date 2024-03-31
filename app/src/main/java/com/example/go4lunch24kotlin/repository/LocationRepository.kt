package com.example.go4lunch24kotlin.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.MainApplication
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationRepository {


    // CHANGE CONST TO ADAPT LOCATION REFRESH
    companion object {
        const val DEFAULT_UPDATE_INTERVAL = 50000
        const val FASTEST_UPDATE_INTERVAL = 20000
    }
    private val locationMutableLiveData = MutableLiveData<Location?>()
    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationRequest() {
        if (callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    locationMutableLiveData.value = location
                }
            }
            MainApplication.getApplication()?.let {
                LocationServices.getFusedLocationProviderClient(it)
                    .requestLocationUpdates(
                        LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(DEFAULT_UPDATE_INTERVAL.toLong())
                            .setFastestInterval(FASTEST_UPDATE_INTERVAL.toLong())
                            .setSmallestDisplacement(50f),
                        callback as LocationCallback,
                        Looper.getMainLooper()
                    )
            }
        }
    }
/*
    fun StopLocationRequest() {
        if (callback != null) {
            MainApplication.getApplication()?.let {
                LocationServices.getFusedLocationProviderClient(it)
                    .removeLocationUpdates(
                        callback!!)
            }
            callback = null
        }
    }

 */

    fun getLocationLiveData(): MutableLiveData<Location?> {
        return locationMutableLiveData
    }
}