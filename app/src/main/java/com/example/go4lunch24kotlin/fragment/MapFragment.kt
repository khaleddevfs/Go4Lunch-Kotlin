package com.example.go4lunch24kotlin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.models.LocationMarker
import com.example.go4lunch24kotlin.ui.RestaurantDetailsActivity
import com.example.go4lunch24kotlin.util.ConverterToBitmap
import com.example.go4lunch24kotlin.viewModel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : SupportMapFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    init {
        getMapAsync(this)
        Log.d("MapFragment", "Map is being initialized...") // Log pour initialisation

    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {

        Log.d("MapFragment", "Map is ready.") // Log lorsque la carte est prête


        // CUSTOM MAP WITHOUT POI WE DON'T NEED
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        // CHECK IF USER CHOSE TO SHARE HIS LOCATION
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MapFragment", "Permissions are granted.") // Log si les permissions sont accordées


            // CONFIGURE MAPVIEWMODEL
            val viewModelFactory = Go4LunchFactory.instance
            val mapViewModel = ViewModelProvider(this, viewModelFactory!!)[MapViewModel::class.java]

            // SET USER LOCATION AND THEN POI
            setUserLocation(mapViewModel, googleMap)

            // SET A LISTENER FOR MARKER CLICK
            googleMap.setOnMarkerClickListener(this)
        } else {
            Log.d("MapFragment", "Location permissions are not granted.") // Log si les permissions ne sont pas accordées
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUserLocation(mapViewModel: MapViewModel, googleMap: GoogleMap) {
        mapViewModel.mapViewStateLocationMarkerMediatorLiveData.observe(this) { (locationMarkerList, userLocation, zoom) ->

            Log.d("MapFragment", "Setting user location and markers.") // Log pour la mise à jour de l'emplacement de l'utilisateur et des marqueurs

            googleMap.clear()

            // MOVE THE CAMERA TO THE USER LOCATION
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom))

            // DISPLAY BLUE DOT FOR USER LOCATION
            googleMap.isMyLocationEnabled = true

            // ZOOM IN, ANIMATE CAMERA
            googleMap.animateCamera(CameraUpdateFactory.zoomIn())

            // CAMERA POSITION
            val cameraPosition = CameraPosition.Builder()
                .target(userLocation) // Sets the center of the map to Mountain View
                .zoom(17f) // Sets the zoom
                .bearing(90f) // Sets the orientation of the camera to east
                .tilt(30f) // Sets the tilt of the camera to 30 degrees
                .build() // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            setLocationMarker(locationMarkerList, googleMap, requireContext())

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("MapFragment", "Marker clicked: ${marker.tag}") // Log lorsqu'un marqueur est cliqué

        val placeId = marker.tag as String?
        startActivity(RestaurantDetailsActivity.navigate(requireContext(), placeId!!))
        return false
    }
/*
    private fun setLocationMarker(locationMarkerList: List<LocationMarker>, googleMap: GoogleMap, context: Context) {
        Log.d("MapFragment", "Setting location markers on map: ${locationMarkerList.size}")
        for (locationMarker in locationMarkerList) {
            Log.d("MapFragment", "Adding marker: ${locationMarker.locationMarkerName}")
            val markerOptions = MarkerOptions()
                .position(locationMarker.locationMarkerLatLng!!)
                .title(locationMarker.locationMarkerName)
                .snippet(locationMarker.locationMarkerAddress)
                .icon(
                    ConverterToBitmap.getBitmapFromVectorDrawable(
                        context,
                        if (locationMarker.isFavorite) R.drawable.icon_location_selected else R.drawable.icon_location_normal
                    )?.let {
                        BitmapDescriptorFactory.fromBitmap(
                            it
                        )
                    }
                )
            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = locationMarker.locationMarkerPlaceId

            if (marker == null) {
                Log.d("MapFragment", "Failed to add marker for: ${locationMarker.locationMarkerName}")
            }
        }
    }

 */

    private fun setLocationMarker(locationMarkerList: List<LocationMarker>, googleMap: GoogleMap, context: Context)  {
        for (locationMarker in locationMarkerList) {
            val marker: Marker = if (locationMarker.isFavorite) {

                setLocationMarkerColor(locationMarker, googleMap, context, R.drawable.icon_location_selected)

            } else {

                // SET TAG TO RETRIEVE THE MARKER IN onMarkerClick METHOD
                setLocationMarkerColor(locationMarker, googleMap, context, R.drawable.icon_location_normal)

            }
            marker.tag = locationMarker.locationMarkerPlaceId
        }
    }


    private fun setLocationMarkerColor(locationMarker: LocationMarker, googleMap: GoogleMap, context: Context, color: Int): Marker {

        return googleMap.addMarker(
            MarkerOptions()
                .position(locationMarker.locationMarkerLatLng!!)
                .title(locationMarker.locationMarkerName)
                .snippet(locationMarker.locationMarkerAddress)
                .icon(
                    ConverterToBitmap.getBitmapFromVectorDrawable(context, color)?.let {
                        BitmapDescriptorFactory
                            .fromBitmap(
                                it
                            )
                    }
                )
        )!!
    }

}
