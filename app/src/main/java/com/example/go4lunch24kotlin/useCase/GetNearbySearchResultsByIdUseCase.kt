package com.example.go4lunch24kotlin.useCase


/*
import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData

import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.Restaurant
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository


class GetNearbySearchResultsByIdUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application,

    ) {

    companion object{
        const val RESTAURANT = "restaurant"
    }

    fun invoke(placeId: String): LiveData<Restaurant> {

        return Transformations.switchMap(locationRepository.getLocationLiveData()) { input: Location ->
            val locationAsText =
                input.latitude.toString() + "," + input.longitude
            Transformations.map(
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius))) { nearbySearchResults: NearbySearchResults? ->
                for (restaurant in nearbySearchResults!!.results!!) {
                    if (restaurant.restaurantId == placeId) {
                        return@map restaurant
                    }
                }
                null
            }
        }
    }
}

 */
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.Restaurant
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository

class GetNearbySearchResultsByIdUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application,
) {

    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(placeId: String): LiveData<Restaurant> {
        val resultLiveData = MediatorLiveData<Restaurant>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            val locationAsText = "${location?.latitude},${location?.longitude}"
            val restaurantListLiveData = nearbySearchRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius)
            )

            resultLiveData.addSource(restaurantListLiveData) { nearbySearchResults ->
                nearbySearchResults?.results?.firstOrNull { it.placeId  == placeId }?.let {
                    resultLiveData.value = it
                }
            }
        }

        return resultLiveData
    }
}
