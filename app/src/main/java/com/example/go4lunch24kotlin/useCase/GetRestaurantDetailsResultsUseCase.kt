package com.example.go4lunch24kotlin.useCase
/*
import android.app.Application
import android.location.Location
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository
import com.example.go4lunch24kotlin.repository.RestaurantDetailsRepository

class GetRestaurantDetailsResultsUseCase(
    locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val restaurantDetailsRepository: RestaurantDetailsRepository,
    private val application: Application,
) {

    companion object{
        const val RESTAURANT = "restaurant"
    }

    private val restaurantDetailsMediatorLiveData = MediatorLiveData<RestaurantDetailsResult>()
    private val restaurantDetailsList = mutableListOf<RestaurantDetailsResult>()

    // GET THE NEARBY SEARCH RESULT WITH USER LOCATION AS TRIGGER
    val nearbySearchResultsLiveData =
        Transformations.switchMap(locationRepository.getLocationLiveData()
        ) { input: Location ->
            val locationAsText =
                input.latitude.toString() + "," + input.longitude
            nearbySearchRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius))
        }


    // THE COMBINE METHOD ALLOW NULL ARGS, SO LETS NEARBY TRIGGER THE COMBINE,
    // THEN, WHEN DETAILS RESULT IS SEND BY REPO, TRIGGER COMBINE TO SET LIVEDATA VALUE
    val invoke =
        MediatorLiveData<List<RestaurantDetailsResult>>().apply {
            addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
                combine(
                    nearbySearchResults,
                    null
                )
            }
            addSource(restaurantDetailsMediatorLiveData) { restaurantDetailsResult ->
                combine(
                    nearbySearchResultsLiveData.value,
                    restaurantDetailsResult
                )
            }
        }

    

    private fun combine(nearbySearchResults: NearbySearchResults?, restaurantDetailsResult: RestaurantDetailsResult?) {
        nearbySearchResults?.results?.let { restaurantList ->

            for(restaurant in restaurantList){
                if (!restaurantDetailsList.contains(restaurantDetailsResult) || restaurantDetailsResult == null){
                    val placeId = restaurant.restaurantId
                    restaurantDetailsMediatorLiveData.addSource(restaurantDetailsRepository.getRestaurantDetailsLiveData(placeId!!)) { restaurantDetailsResult ->
                        restaurantDetailsList.add(restaurantDetailsResult!!)
                        restaurantDetailsMediatorLiveData.setValue(restaurantDetailsResult)

                    }
                }
            }
            invoke.setValue(restaurantDetailsList)
        }
    }
}



import android.app.Application
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository
import com.example.go4lunch24kotlin.repository.RestaurantDetailsRepository

import androidx.lifecycle.LiveData


class GetRestaurantDetailsResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val restaurantDetailsRepository: RestaurantDetailsRepository,
    private val application: Application,
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<List<RestaurantDetailsResult>> {
        val restaurantDetailsLiveData = MediatorLiveData<List<RestaurantDetailsResult>>()
        val restaurantDetailsList = mutableListOf<RestaurantDetailsResult>()

        val locationLiveData = locationRepository.getLocationLiveData()

        restaurantDetailsLiveData.addSource(locationLiveData) { location ->
            val locationAsText = "${location?.latitude},${location?.longitude}"
            val nearbySearchLiveData = nearbySearchRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius)
            )

            restaurantDetailsLiveData.addSource(nearbySearchLiveData) { nearbySearchResults ->
                nearbySearchResults?.results?.forEach { restaurant ->
                    val placeId = restaurant.placeId
                    val detailsLiveData = restaurantDetailsRepository.getRestaurantDetailsLiveData(placeId!!)
                    restaurantDetailsLiveData.addSource(detailsLiveData) { restaurantDetailsResult ->
                        if (!restaurantDetailsList.any { it.result?.placeId == placeId }) {
                            restaurantDetailsList.add(restaurantDetailsResult!!)
                            restaurantDetailsLiveData.value = restaurantDetailsList.toList()
                            // After updating LiveData, you might want to remove this particular source
                            // to prevent unnecessary updates or manage them as needed.
                        }
                    }
                }
            }
        }

        return restaurantDetailsLiveData
    }
}
 */
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository
import com.example.go4lunch24kotlin.repository.RestaurantDetailsRepository

class GetRestaurantDetailsResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val restaurantDetailsRepository: RestaurantDetailsRepository,
    private val application: Application
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<List<RestaurantDetailsResult>> {
        val restaurantDetailsLiveData = MediatorLiveData<List<RestaurantDetailsResult>>()
        val restaurantDetailsList = mutableListOf<RestaurantDetailsResult>()

        val locationLiveData = locationRepository.getLocationLiveData()

        // Ajout du LiveData du NearbySearchRepository comme source
        restaurantDetailsLiveData.addSource(nearbySearchRepository.restaurantListLiveData) { nearbySearchResults ->
            nearbySearchResults?.results?.forEach { restaurant ->
                val placeId = restaurant.restaurantId
                // Assurez-vous que getRestaurantDetailsLiveData() existe et fonctionne comme prévu
                val detailsLiveData = restaurantDetailsRepository.getRestaurantDetailsLiveData(placeId!!)
                restaurantDetailsLiveData.addSource(detailsLiveData) { restaurantDetailsResult ->
                    if (restaurantDetailsResult != null && !restaurantDetailsList.any { it.result?.placeId == placeId }) {
                        restaurantDetailsList.add(restaurantDetailsResult)
                        restaurantDetailsLiveData.value = restaurantDetailsList.toList()
                    }
                }
            }
        }

        // Observe location changes to trigger NearbySearchRepository updates
        restaurantDetailsLiveData.addSource(locationLiveData) { location ->
            if (location != null) {
                val locationAsText = "${location.latitude},${location.longitude}"
                // Cela déclenchera une mise à jour interne dans NearbySearchRepository
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)
                )
            }
        }

        return restaurantDetailsLiveData
    }
}
