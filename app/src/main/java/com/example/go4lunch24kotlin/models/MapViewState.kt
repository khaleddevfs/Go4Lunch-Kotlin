package com.example.go4lunch24kotlin.models

import com.google.android.gms.maps.model.LatLng



data class MapViewState constructor(
    val locationMarkerList: List<LocationMarker>,
    val latLng: LatLng,
    val zoom: Float
)

