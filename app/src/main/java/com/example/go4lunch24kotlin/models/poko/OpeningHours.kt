package com.example.go4lunch24kotlin.models.poko


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OpeningHours constructor(
    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null,
    @SerializedName("periods")
    @Expose
    var periods: List<Periods>? = null
)

