package com.example.go4lunch24kotlin.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.Predictions
import com.example.go4lunch24kotlin.services.GooglePlacesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutocompleteRepository (
    private val googleMapsApi: GooglePlacesService,
    private val application: Application,
) {
    fun getAutocompleteResultListLiveData(
        location: String?,
        input: String?,
    ): LiveData<Predictions?> {

        val key = application.getString(R.string.MAPS_API_KEY)
        val type = application.getString(R.string.autocomplete_type)
        val radius = application.getString(R.string.radius)

        val autocompleteResultMutableLiveData = MutableLiveData<Predictions?>()

        googleMapsApi.autocompleteResult(key, type, location, radius, input)!!.enqueue(
            object : Callback<Predictions?> {
                override fun onResponse(
                    call: Call<Predictions?>,
                    response: Response<Predictions?>,
                ) {
                    if (response.body() != null) {
                        autocompleteResultMutableLiveData.value = response.body()
                    }
                }
                override fun onFailure(call: Call<Predictions?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        return autocompleteResultMutableLiveData
    }
}