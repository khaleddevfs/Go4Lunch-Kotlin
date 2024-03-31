package com.example.go4lunch24kotlin.models.poko


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceAutocompleteStructuredFormat constructor(
    @SerializedName("main_text")
    @Expose
    var name: String? = null
)