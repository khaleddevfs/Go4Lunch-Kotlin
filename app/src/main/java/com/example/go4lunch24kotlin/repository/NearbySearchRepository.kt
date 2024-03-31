package com.example.go4lunch24kotlin.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.services.GooglePlacesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbySearchRepository (
    private val googleMapsApi: GooglePlacesService,
    private val application: Application,

    ) {

    private val cache: MutableMap<String, NearbySearchResults> = HashMap(2000)

    fun getRestaurantListLiveData(
        type: String,
        location: String,
        radius: String,
    ): LiveData<NearbySearchResults?> {

        val key = application.getString(R.string.MAPS_API_KEY)

        val nearbySearchResultsMutableLiveData = MutableLiveData<NearbySearchResults?>()

        val nearbySearchResults = cache[location]

        if (nearbySearchResults != null) {

            nearbySearchResultsMutableLiveData.value = nearbySearchResults

        } else {
            googleMapsApi.searchRestaurant(key, type, location, radius)!!.enqueue(
                object : Callback<NearbySearchResults?> {
                    override fun onResponse(
                        call: Call<NearbySearchResults?>,
                        response: Response<NearbySearchResults?>,
                    ){
                        if (response.isSuccessful && response.body() != null) {
                            cache[location] = response.body()!!
                            nearbySearchResultsMutableLiveData.value = response.body()
                        } else {
                            Log.e("NearbySearchRepo", "Erreur API: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<NearbySearchResults?>, t: Throwable) {
                        t.printStackTrace()
                    }


                })
        }
        return nearbySearchResultsMutableLiveData
    }
}