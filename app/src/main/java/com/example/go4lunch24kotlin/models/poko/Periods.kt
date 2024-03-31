package com.example.go4lunch24kotlin.models.poko

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Periods constructor(
    @SerializedName("close")
    @Expose
    var close: Close? = null,
    @SerializedName("open")
    @Expose
    var open: Open? = null
)

