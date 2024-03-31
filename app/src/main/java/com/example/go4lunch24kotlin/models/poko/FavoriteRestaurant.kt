package com.example.go4lunch24kotlin.models.poko

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FavoriteRestaurant constructor(
    @SerializedName("place_id")
    @Expose
    var restaurantId: String? = null
)
