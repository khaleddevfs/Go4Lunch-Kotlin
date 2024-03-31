package com.example.go4lunch24kotlin.models.poko

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RestaurantLatLngLiteral constructor(
    @SerializedName("lat")
    @Expose
    var lat: Double? = null,
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
)