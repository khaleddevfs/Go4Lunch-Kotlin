package com.example.go4lunch24kotlin.models.poko

import com.example.go4lunch24kotlin.models.poko.OpeningHours
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RestaurantDetails constructor(
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null,
    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null,
    @SerializedName("formatted_phone_number")
    @Expose
    var formattedPhoneNumber: String? = null,
    @SerializedName("website")
    @Expose
    var website: String? = null
)