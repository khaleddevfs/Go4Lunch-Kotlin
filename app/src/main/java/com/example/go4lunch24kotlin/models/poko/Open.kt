package com.example.go4lunch24kotlin.models.poko

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Open constructor(
    @SerializedName("day")
    @Expose
    var day: Int? = null,
    @SerializedName("time")
    @Expose
    var time: String? = null,
)

