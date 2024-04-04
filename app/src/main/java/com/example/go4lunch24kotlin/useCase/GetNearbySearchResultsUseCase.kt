package com.example.go4lunch24kotlin.useCase
/*
import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository


class GetNearbySearchResultsUseCase(
    locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application,

    ) {

    companion object{
        const val RESTAURANT = "restaurant"
    }

    var invoke: LiveData<NearbySearchResults> =
        Transformations.switchMap(locationRepository.getLocationLiveData()) { input: Location ->
            val locationAsText = input.latitude.toString() + "," + input.longitude
            nearbySearchRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius))
        }

}



import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository

class GetNearbySearchResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<NearbySearchResults> {
        val resultLiveData = MediatorLiveData<NearbySearchResults>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            if (location != null) {
                updateNearbySearchResults(location, resultLiveData)
            }
        }

        return resultLiveData
    }

    private fun updateNearbySearchResults(location: Location, resultLiveData: MediatorLiveData<NearbySearchResults>) {
        val locationAsText = "${location.latitude},${location.longitude}"
        val nearbySearchLiveData = nearbySearchRepository.getRestaurantListLiveData(
            RESTAURANT,
            locationAsText,
            application.getString(R.string.radius)
        )

        // Temporary add source to mediator
        resultLiveData.addSource(nearbySearchLiveData) { nearbySearchResults ->
            resultLiveData.value = nearbySearchResults
            // Once the data is set, remove the source to avoid unnecessary updates
            resultLiveData.removeSource(nearbySearchLiveData)
        }
    }
}


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository

class GetNearbySearchResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<NearbySearchResults?> {
        val resultLiveData = MediatorLiveData<NearbySearchResults?>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            if (location != null) {
                val locationAsText = "${location.latitude},${location.longitude}"
                // Trigger la mise à jour des données dans le repository
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)
                )
                // Ajoutez le LiveData interne du repository comme source pour le MediatorLiveData
                resultLiveData.addSource(nearbySearchRepository.restaurantListLiveData) { nearbySearchResults ->
                    resultLiveData.value = nearbySearchResults
                    // Vous pourriez vouloir supprimer cette source après la mise à jour pour éviter des mises à jour inutiles
                    // mais cela dépend de la logique de votre application
                }
            }
        }

        return resultLiveData
    }
}

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository

class GetNearbySearchResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<NearbySearchResults?> {
        val resultLiveData = MediatorLiveData<NearbySearchResults?>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            location?.let {
                val locationAsText = "${it.latitude},${it.longitude}"
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)
                )

                // Ici, on suppose que votre logique d'application nécessite de ne pas continuer à écouter les anciennes valeurs.
                // Cette implémentation pourrait varier selon vos besoins spécifiques.
                resultLiveData.addSource(nearbySearchRepository.restaurantListLiveData) { nearbySearchResults ->
                    resultLiveData.value = nearbySearchResults
                    // Supprimer cette source après avoir reçu la mise à jour pour éviter des mises à jour répétitives.
                    resultLiveData.removeSource(nearbySearchRepository.restaurantListLiveData)
                }
            }
        }

        return resultLiveData
    }
}

 */

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository

class GetNearbySearchResultsUseCase(
    private val locationRepository: LocationRepository,
    private val nearbySearchRepository: NearbySearchRepository,
    private val application: Application
) {
    companion object {
        const val RESTAURANT = "restaurant"
    }

    fun invoke(): LiveData<NearbySearchResults?> {
        val resultLiveData = MediatorLiveData<NearbySearchResults?>()

        val locationLiveData = locationRepository.getLocationLiveData()

        resultLiveData.addSource(locationLiveData) { location ->
            location?.let {
                val locationAsText = "${it.latitude},${it.longitude}"
                nearbySearchRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)
                )

                resultLiveData.addSource(nearbySearchRepository.restaurantListLiveData) { nearbySearchResults ->
                    resultLiveData.value = nearbySearchResults
                    resultLiveData.removeSource(nearbySearchRepository.restaurantListLiveData)
                }
            } ?: run {
                // Gérer le cas où la location est nulle, si nécessaire
            }
        }

        return resultLiveData
    }
}
