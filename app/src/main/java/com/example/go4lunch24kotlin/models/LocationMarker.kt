package com.example.go4lunch24kotlin.models

import com.google.android.gms.maps.model.LatLng

data class LocationMarker constructor(
    val locationMarkerName: String? = null,
    val locationMarkerPlaceId: String? = null,
    val locationMarkerAddress: String? = null,
    val locationMarkerLatLng: LatLng? = null,
    val isFavorite: Boolean = false
)






