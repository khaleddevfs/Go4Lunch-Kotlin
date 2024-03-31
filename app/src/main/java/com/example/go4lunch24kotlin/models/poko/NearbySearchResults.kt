package com.example.go4lunch24kotlin.models.poko

import com.example.go4lunch24kotlin.models.Restaurant
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NearbySearchResults constructor(
    // ADD EACH RESTAURANTS SEARCH IN A LIST
    @SerializedName("results")
    @Expose
    var results: List<Restaurant>? = null
)