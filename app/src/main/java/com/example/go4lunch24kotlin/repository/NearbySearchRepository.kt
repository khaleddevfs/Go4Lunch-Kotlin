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
/*
class NearbySearchRepository (
    private val googleMapsApi: GooglePlacesService,
    private val application: Application,


) {
    private val nearbySearchResultsMutableLiveData = MutableLiveData<NearbySearchResults?>()

    val restaurantListLiveData: LiveData<NearbySearchResults?> = nearbySearchResultsMutableLiveData

    private val cache: MutableMap<String, NearbySearchResults> = HashMap(2000)

    fun getRestaurantListLiveData(type: String, location: String, radius: String) {
        val key = application.getString(R.string.MAPS_API_KEY)
        val nearbySearchResults = cache[location]

        if (nearbySearchResults != null) {
            nearbySearchResultsMutableLiveData.value = nearbySearchResults
        } else {
            googleMapsApi.searchRestaurant(key, type, location, radius)?.enqueue(object : Callback<NearbySearchResults?> {
                override fun onResponse(call: Call<NearbySearchResults?>, response: Response<NearbySearchResults?>) {
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
    }

}

 */



class NearbySearchRepository(
    private val googleMapsApi: GooglePlacesService,
    application: Application
) {
    private val apiKey: String = application.getString(R.string.MAPS_API_KEY)
    private val nearbySearchResultsMutableLiveData = MutableLiveData<NearbySearchResults?>()
    val restaurantListLiveData: LiveData<NearbySearchResults?> = nearbySearchResultsMutableLiveData
    private val cache: MutableMap<String, NearbySearchResults> = HashMap()

    fun getRestaurantListLiveData(type: String, location: String, radius: String) {
        val cacheKey = "$type|$location|$radius"
        cache[cacheKey]?.let {
            nearbySearchResultsMutableLiveData.value = it
            return
        }

        googleMapsApi.searchRestaurant(apiKey, type, location, radius)?.enqueue(object : Callback<NearbySearchResults?> {
            override fun onResponse(call: Call<NearbySearchResults?>, response: Response<NearbySearchResults?>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        cache[cacheKey] = it
                        nearbySearchResultsMutableLiveData.value = it
                    }
                } else {
                    Log.e("NearbySearchRepo", "API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NearbySearchResults?>, t: Throwable) {
                Log.e("NearbySearchRepo", "Network Error: ", t)
            }
        })
    }
}
