package com.example.go4lunch24kotlin.useCase


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
/*
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

 */

    fun invoke(placeId: String): LiveData<Restaurant?> {
        val resultLiveData = MediatorLiveData<Restaurant?>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            if (location != null) {
                val locationAsText = "${location.latitude},${location.longitude}"
                // Mise à jour pour appeler `getRestaurantListLiveData` sans attendre un retour
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)
                )
                // Utilisez directement `restaurantListLiveData` du repository
                resultLiveData.addSource(nearbySearchRepository.restaurantListLiveData) { nearbySearchResults ->
                    val restaurant = nearbySearchResults?.results?.firstOrNull { it.restaurantId == placeId }
                    resultLiveData.value = restaurant
                }
            }
        }

        return resultLiveData
    }

}

