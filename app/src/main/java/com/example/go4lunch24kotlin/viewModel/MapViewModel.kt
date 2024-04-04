package com.example.go4lunch24kotlin.viewModel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.go4lunch24kotlin.models.LocationMarker
import com.example.go4lunch24kotlin.models.MapViewState
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.UserSearchRepository
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.useCase.GetNearbySearchResultsUseCase
import com.google.android.gms.maps.model.LatLng

class MapViewModel(
    locationRepository: LocationRepository,
    getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase,
    workMateRestaurantChoice: WorkMatesChoiceRepository,
    userSearchRepository: UserSearchRepository
) : ViewModel() {
    companion object {
        private const val ZOOM_FOCUS = 15f
    }

    private var locationLiveData: MutableLiveData<Location?> =
        locationRepository.getLocationLiveData()
    private var nearbySearchResultsLiveData: LiveData<NearbySearchResults?> =
        getNearbySearchResultsUseCase.invoke()
    private var workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<WorkMateRestaurantChoice>> =
        workMateRestaurantChoice.getWorkmatesRestaurantChoice()
    private var usersSearchLiveData: LiveData<String> =
        userSearchRepository.getUsersSearchLiveData()

    val mapViewStateLocationMarkerMediatorLiveData = MediatorLiveData<MapViewState>().apply {
        addSource(locationLiveData) { location ->
            combine(
                location,
                nearbySearchResultsLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearchLiveData.value
            )
        }
        addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
            combine(
                locationLiveData.value,
                nearbySearchResults,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearchLiveData.value
            )
        }
        addSource(workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
            combine(
                locationLiveData.value,
                nearbySearchResultsLiveData.value,
                userWithFavoriteRestaurants,
                usersSearchLiveData.value
            )
        }
        addSource(usersSearchLiveData) { usersSearch ->
            combine(
                locationLiveData.value,
                nearbySearchResultsLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearch
            )
        }
    }

    private fun combine(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        usersSearch: String?
    ) {
        Log.d(
            "MapViewModel",
            "Combine called with usersSearch: $usersSearch"
        ) // Log pour la fonction combine
        if (!usersSearch.isNullOrEmpty()) {
            Log.d("MapViewModel", "Processing mapUsersSearch") // Avant d'appeler mapUsersSearch
            // Vérifiez que `nearbySearchResults` et `location` ne sont pas null avant d'appeler `mapUsersSearch`
            if (nearbySearchResults != null && location != null) {
                mapViewStateLocationMarkerMediatorLiveData.value = mapUsersSearch(
                    location,
                    nearbySearchResults,
                    workMateRestaurantChoice,
                    usersSearch
                )
            }
        } else if (nearbySearchResults != null && location != null) {
            Log.d(
                "MapViewModel",
                "Processing map with nearbySearchResults and location"
            ) // Avant d'appeler map
            mapViewStateLocationMarkerMediatorLiveData.value = map(
                location,
                nearbySearchResults,
                workMateRestaurantChoice
            )
        }
    }

    /*   private fun combine(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        usersSearch: String?
    )

    {
        Log.d("MapViewModel", "Combine called with usersSearch: $usersSearch") // Log pour la fonction combine
        if (!usersSearch.isNullOrEmpty()) {
            Log.d("MapViewModel", "Processing mapUsersSearch") // Avant d'appeler mapUsersSearch

            mapViewStatePoiMediatorLiveData.value = mapUsersSearch(
                location,
                nearbySearchResults,
                workMateRestaurantChoice,
                usersSearch
            )
        } else if (nearbySearchResults != null && location != null) {
            Log.d("MapViewModel", "Processing map with nearbySearchResults and location") // Avant d'appeler map

            mapViewStatePoiMediatorLiveData.value = map(
                location,
                nearbySearchResults,
                workMateRestaurantChoice
            )
        }
    }

  */

    // MAP WITH USER'S SEARCH ONLY
    private fun mapUsersSearch(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        usersSearch: String
    ): MapViewState {
        Log.d("MapViewModel", "mapUsersSearch: Processing search for $usersSearch")

        val locationMarkerList = mutableListOf<LocationMarker>()
        val restaurantAsFavoriteId = mutableListOf<String>()

        // Add id from restaurant who as been set as favorite
        if (workMateRestaurantChoice != null) {
            for (i in workMateRestaurantChoice.indices) {
                restaurantAsFavoriteId.add(workMateRestaurantChoice[i].restaurantId!!)
            }
        }
        Log.d(
            "MapViewModel",
            "nearbySearchResults contains ${nearbySearchResults?.results?.size ?: "null"} results"
        )

        for (i in nearbySearchResults!!.results!!.indices) {
            if (nearbySearchResults.results!![i].restaurantName!!.contains(usersSearch)) {
                locationMarkerList.add(locationMarker(nearbySearchResults, i, workMateRestaurantChoice, restaurantAsFavoriteId))
            }
        }
        val userLocation = LatLng(
            location!!.latitude,
            location.longitude
        )

        return MapViewState(
            locationMarkerList,
            LatLng(
                userLocation.latitude,
                userLocation.longitude
            ),
            Companion.ZOOM_FOCUS
        ).also {
            Log.d(
                "MapViewModel",
                "mapUsersSearch: Created MapViewState with ${locationMarkerList.size} markers"
            ) // Log après création de MapViewState
        }
    }


    private fun map(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
    ): MapViewState {

        Log.d("MapViewModel", "map: Processing general map view state")

        val locationMarkerList = mutableListOf<LocationMarker>()
        val restaurantAsFavoriteId = mutableListOf<String>()

        // Add id from restaurant who as been set as favorite
        if (workMateRestaurantChoice != null) {
            for (i in workMateRestaurantChoice.indices) {
                restaurantAsFavoriteId.add(workMateRestaurantChoice[i].restaurantId!!)
            }
        }

        for (i in nearbySearchResults!!.results!!.indices) {
            locationMarkerList.add(
                locationMarker(
                    nearbySearchResults,
                    i,
                    workMateRestaurantChoice,
                    restaurantAsFavoriteId
                )
            )
        }

        val userLocation = LatLng(
            location!!.latitude,
            location.longitude
        )

        return MapViewState(
            locationMarkerList,
            LatLng(
                userLocation.latitude,
                userLocation.longitude
            ),
            ZOOM_FOCUS
        ).also {
            Log.d(
                "MapViewModel",
                "map: Created MapViewState with ${locationMarkerList.size} markers"
            ) // Log après création de MapViewState
        }
    }




    private fun locationMarker(
        nearbySearchResults: NearbySearchResults,
        i: Int,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        restaurantAsFavoriteId : List<String>
    ): LocationMarker {
        var isFavorite = false
        val locationMarkerName = nearbySearchResults.results!![i].restaurantName
        val locationMarkerPlaceId = nearbySearchResults.results!![i].restaurantId
        val locationMarkerAddress = nearbySearchResults.results!![i].restaurantAddress
        val locationMarkerLatLng = LatLng(
            nearbySearchResults
                .results!![i]
                .restaurantGeometry?.restaurantLatLngLiteral?.lat!!,
            nearbySearchResults
                .results!![i]
                .restaurantGeometry?.restaurantLatLngLiteral?.lng!!
        )

        if (workMateRestaurantChoice != null
            && restaurantAsFavoriteId.contains(locationMarkerPlaceId)
        ) {
            isFavorite = true
        }

    return  LocationMarker(
    locationMarkerName,
    locationMarkerPlaceId,
    locationMarkerAddress,
    locationMarkerLatLng,
    isFavorite
    ).also {
        Log.d(
            "MapViewModel",
            "locationMarker: Created marker for $locationMarkerName, favorite: $isFavorite"
        ) // Log pour chaque marqueur créé
    }

}
}