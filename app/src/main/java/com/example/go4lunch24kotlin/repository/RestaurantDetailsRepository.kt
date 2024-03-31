package com.example.go4lunch24kotlin.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.services.GooglePlacesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailsRepository(
    private val googlePlacesService: GooglePlacesService,
    private val application: Application,
) {


    private val cache: MutableMap<String, RestaurantDetailsResult?> = HashMap(2000)

    fun getRestaurantDetailsLiveData(restaurantId: String): LiveData<RestaurantDetailsResult?> {

        val key = application.getString(R.string.MAPS_API_KEY)
        val FIELDS: String = application.getString(R.string.restaurant_details_fields)

        val placeDetailsResultMutableLiveData = MutableLiveData<RestaurantDetailsResult?>()

        val restaurantDetailsResult: RestaurantDetailsResult? = cache[restaurantId]

        if (restaurantDetailsResult != null) {

            placeDetailsResultMutableLiveData.value = restaurantDetailsResult

        } else {

            val call = googlePlacesService.searchRestaurantDetails(key, restaurantId, FIELDS)

            call!!.enqueue(object : Callback<RestaurantDetailsResult?> {
                override fun onResponse(
                    call: Call<RestaurantDetailsResult?>,
                    response: Response<RestaurantDetailsResult?>,
                ) {
                    if (response.body() != null) {
                        cache[restaurantId] = response.body()
                        placeDetailsResultMutableLiveData.setValue(response.body())
                    } else {
                        Log.d("Response errorBody", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<RestaurantDetailsResult?>, t: Throwable) {
                    Log.d("pipo", "Detail called issues")
                }
            })
        }
        return placeDetailsResultMutableLiveData
    }

}