package com.example.go4lunch24kotlin.models.poko

import com.google.gson.annotations.SerializedName


data class RestaurantDetailsResult(
    @SerializedName("result")
    var result: RestaurantDetails? = null,

)