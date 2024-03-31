package com.example.go4lunch24kotlin.models.poko


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Prediction constructor(
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("structured_formatting")
    @Expose
    var structuredFormatting: PlaceAutocompleteStructuredFormat? = null,
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

)