package com.example.go4lunch24kotlin.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.BuildConfig
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.adapters.RestaurantDetailsViewStateAdapter
import com.example.go4lunch24kotlin.models.Restaurant
import com.example.go4lunch24kotlin.models.RestaurantDetailsViewState
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.Workmate
import com.example.go4lunch24kotlin.models.poko.FavoriteRestaurant
import com.example.go4lunch24kotlin.models.poko.Photo
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.FavoriteRestaurantRepository
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.repository.WorkMatesRepository
import com.example.go4lunch24kotlin.useCase.ClickOnChoseRestaurantButtonUseCase
import com.example.go4lunch24kotlin.useCase.ClickOnFavoriteRestaurantUseCase
import com.example.go4lunch24kotlin.useCase.GetCurrentUserIdUseCase
import com.example.go4lunch24kotlin.useCase.GetNearbySearchResultsByIdUseCase
import com.example.go4lunch24kotlin.useCase.GetRestaurantDetailsResultsByIdUseCase
import kotlin.math.roundToInt

class RestaurantDetailsViewModel constructor(
    private val application: Application,
    private val getNearbySearchResultsByIdUseCase: GetNearbySearchResultsByIdUseCase,
    private val getRestaurantDetailsResultsByIdUseCase: GetRestaurantDetailsResultsByIdUseCase,
    private val workMatesChoiceRepository: WorkMatesChoiceRepository,
    private val workmatesRepository: WorkMatesRepository,
    private val favoriteRestaurantsRepository: FavoriteRestaurantRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val clickOnChoseRestaurantButtonUseCase: ClickOnChoseRestaurantButtonUseCase,
    private val clickOnFavoriteRestaurantUseCase: ClickOnFavoriteRestaurantUseCase,
) : ViewModel() {

    val restaurantDetailsViewStateMediatorLiveData =
        MediatorLiveData<RestaurantDetailsViewState>()
    val workmatesLikeThisRestaurantMediatorLiveData =
        MediatorLiveData<List<RestaurantDetailsViewStateAdapter>>()

    // INIT THE VIEW MODEL WITH THE PLACEID SEND IN THE INTENT
    fun init(placeId: String) {
        val restaurantLiveData: LiveData<Restaurant> =
            getNearbySearchResultsByIdUseCase.invoke(placeId)
        val restaurantDetailsLiveData: LiveData<RestaurantDetailsResult?> =
            getRestaurantDetailsResultsByIdUseCase.invoke(placeId)
        val workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<WorkMateRestaurantChoice>> =
            workMatesChoiceRepository.getWorkmatesRestaurantChoice()
        val favoriteRestaurantsLiveData: LiveData<List<FavoriteRestaurant>> =
            favoriteRestaurantsRepository.getFavoriteRestaurants()
        val workMatesLiveData: LiveData<List<Workmate>> = workmatesRepository.getWorkmates()


        // OBSERVERS FOR RESTAURANT DETAILS
        restaurantDetailsViewStateMediatorLiveData.addSource(restaurantLiveData) { restaurant ->
            combine(
                restaurant,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsLiveData.value,
                favoriteRestaurantsLiveData.value)
        }

        restaurantDetailsViewStateMediatorLiveData.addSource(restaurantDetailsLiveData) { restaurantDetailsResults ->
            combine(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsResults,
                favoriteRestaurantsLiveData.value)
        }

        restaurantDetailsViewStateMediatorLiveData.addSource(
            workmatesWhoMadeRestaurantChoiceLiveData) { workmatesWithFavoriteRestaurant ->
            combine(
                restaurantLiveData.value,
                workmatesWithFavoriteRestaurant,
                restaurantDetailsLiveData.value,
                favoriteRestaurantsLiveData.value)
        }


        restaurantDetailsViewStateMediatorLiveData.addSource(favoriteRestaurantsLiveData) { favoriteRestaurants ->
            combine(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsLiveData.value,
                favoriteRestaurants)
        }


        // OBSERVERS FOR WORKMATES RECYCLERVIEW
        workmatesLikeThisRestaurantMediatorLiveData.addSource(restaurantLiveData) { restaurantSearch ->
            combineWorkmates(
                restaurantSearch,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                workMatesLiveData.value)
        }
        workmatesLikeThisRestaurantMediatorLiveData.addSource(
            workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
            combineWorkmates(
                restaurantLiveData.value,
                userWithFavoriteRestaurants,
                workMatesLiveData.value)
        }

        workmatesLikeThisRestaurantMediatorLiveData.addSource(workMatesLiveData) { userModels ->
            combineWorkmates(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                userModels)
        }


    }

    private fun combine(
        restaurant: Restaurant?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        restaurantDetails: RestaurantDetailsResult?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ) {

        if (restaurant != null && restaurantDetails == null) {
            restaurantDetailsViewStateMediatorLiveData.setValue(mapWithoutDetails(
                restaurant,
                workMateRestaurantChoice,
                favoriteRestaurants))
        } else if (restaurant != null && favoriteRestaurants != null) {
            restaurantDetailsViewStateMediatorLiveData.value = map(
                restaurant,
                workMateRestaurantChoice,
                restaurantDetails,
                favoriteRestaurants)
        }
    }

    private fun combineWorkmates(
        restaurant: Restaurant?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        users: List<Workmate>?,
    ) {

        if (workMateRestaurantChoice != null && users != null && restaurant != null) {
            workmatesLikeThisRestaurantMediatorLiveData.value = mapWorkmates(
                restaurant,
                workMateRestaurantChoice,
                users)
        }
    }

    @SuppressLint("ResourceType")
    private fun mapWithoutDetails(
        restaurant: Restaurant,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ): RestaurantDetailsViewState {

        val userId: String = getCurrentUserIdUseCase.invoke()

        var restaurantChoiceState: Int = R.drawable.ic_baseline_uncheck_circle_24
        var backgroundVectorColor =
            Color.parseColor(application.getString(R.string.background_black))

        // CHECK IF USER HAVE A RESTAURANT CHOICE AND SET THE CHOICE ICON PROPERLY
        if (isUserGetRestaurantChoice(workMateRestaurantChoice, restaurant, userId)) {
            restaurantChoiceState = R.drawable.ic_baseline_check_circle_24_ok
            backgroundVectorColor =
                Color.parseColor(application.getString(R.string.background_green))
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        var detailLikeButton = R.drawable.ic_baseline_star_border_24
        favoriteRestaurants?.let { favoriteRestaurantList ->
            for (restaurantInFavorite in favoriteRestaurantList) {
                if (restaurantInFavorite.restaurantId!! == restaurant.placeId)
                    detailLikeButton = R.drawable.ic_baseline_star_rate_24
            }
        }

        return RestaurantDetailsViewState(
            restaurant.restaurantName,
            restaurant.restaurantAddress,
            application.getString(R.string.api_url)
                    + application.getString(R.string.photo_reference)
                    + photoReference(restaurant.restaurantPhotos)
                    + application.getString(R.string.and_key)
                    + application.getString(R.string.MAPS_API_KEY),
            application.getString(R.string.phone_number_unavailable),
            application.getString(R.string.website_unavailable),
            restaurant.placeId,
            convertRatingStars(restaurant.rating),
            restaurantChoiceState,
            detailLikeButton,
            backgroundVectorColor
        )
    }

    @SuppressLint("ResourceType")
    private fun map(
        restaurant: Restaurant,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        restaurantDetails: RestaurantDetailsResult?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ): RestaurantDetailsViewState {

        val userId = getCurrentUserIdUseCase.invoke()
        var restaurantChoiceState: Int = R.drawable.ic_baseline_uncheck_circle_24
        var backgroundVectorColor =
            Color.parseColor(application.getString(R.string.background_black))
        var restaurantPhoneNumber: String
        var restaurantWebsite: String

        // CHECK IF USER HAVE A RESTAURANT CHOICE AND SET THE CHOICE ICON PROPERLY
        if (isUserGetRestaurantChoice(workMateRestaurantChoice, restaurant, userId)) {
            restaurantChoiceState = R.drawable.ic_baseline_check_circle_24_ok
            backgroundVectorColor =
                Color.parseColor(application.getString(R.string.background_green))
        }

        // CHECK IF PHONE NUMBER IS AVAILABLE
        restaurantPhoneNumber = application.getString(R.string.phone_number_unavailable)
        restaurantDetails?.result?.formattedPhoneNumber?.let {
            restaurantPhoneNumber = restaurantDetails.result!!.formattedPhoneNumber!!
        }

        // CHECK IF WEBSITE ADDRESS IS AVAILABLE
        restaurantWebsite = application.getString(R.string.website_unavailable)
        restaurantDetails?.result?.website?.let {
            restaurantWebsite = restaurantDetails.result!!.website!!
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        var detailLikeButton = R.drawable.ic_baseline_star_border_24
        favoriteRestaurants?.let { favoriteRestaurantList ->
            for (restaurantInFavorite in favoriteRestaurantList) {
                if (restaurantInFavorite.restaurantId!! == restaurant.placeId)
                    detailLikeButton = R.drawable.ic_baseline_star_rate_24
            }
        }

        return RestaurantDetailsViewState(
            restaurant.restaurantName,
            restaurant.restaurantAddress,
            application.getString(R.string.api_url)
                    + application.getString(R.string.photo_reference)
                    + photoReference(restaurant.restaurantPhotos)
                    + application.getString(R.string.and_key)
                    + application.getString(R.string.MAPS_API_KEY),
            restaurantPhoneNumber,
            restaurantWebsite,
            restaurant.placeId,
            convertRatingStars(restaurant.rating),
            restaurantChoiceState,
            detailLikeButton,
            backgroundVectorColor
        )
    }

    private fun mapWorkmates(
        restaurant: Restaurant,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>,
        users: List<Workmate>,
    ): List<RestaurantDetailsViewStateAdapter> {

        val workMatesViewStateList = mutableListOf<RestaurantDetailsViewStateAdapter>()

        for (workMateChoice in workMateRestaurantChoice) {
            if (workMateChoice.restaurantId == restaurant.placeId) {
                for (user in users) {
                    if (user.uid == workMateChoice.userId) {
                        val name = user.userName + " " + application.getString(R.string.is_joining)
                        val avatar = user.avatarURL
                        workMatesViewStateList.add(RestaurantDetailsViewStateAdapter(
                            name,
                            avatar!!
                        ))
                    }
                }
            }
        }
        return workMatesViewStateList
    }

    private fun isUserGetRestaurantChoice(
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        restaurant: Restaurant,
        userId: String?,
    ): Boolean {

        workMateRestaurantChoice?.let {
            for (user in workMateRestaurantChoice) {
                if (user.restaurantId == restaurant.placeId && user.userId == userId) {
                    return true
                }
            }
        }
        return false
    }

    private fun photoReference(restaurantPhotos: List<Photo>?): String {

        restaurantPhotos?.let { photoList ->
            for (photo in photoList)
                if (photo.photoReference!!.isNotEmpty()) {
                    return photo.photoReference!!
                }
        }
        return application.getString(R.string.photo_unavailable)
    }

    private fun convertRatingStars(rating: Double): Double {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return (rating * 3 / 5).roundToInt().toDouble()
    }

    // CLICK ON THE CHOSE FAB
    fun onChoseRestaurantButtonClick(
        restaurantId: String,
        restaurantName: String,
        restaurantAddress: String,
    ) {
        clickOnChoseRestaurantButtonUseCase.onRestaurantSelectedClick(
            restaurantId,
            restaurantName,
            restaurantAddress)
    }

    // CLICK ON THE FAVORITE ICON
    fun onFavoriteIconClick(
        restaurantId: String,
        restaurantName: String,
    ) {
        clickOnFavoriteRestaurantUseCase.onFavoriteRestaurantClick(restaurantId, restaurantName)
    }
}