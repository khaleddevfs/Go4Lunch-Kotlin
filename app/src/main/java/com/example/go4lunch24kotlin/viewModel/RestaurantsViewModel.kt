package com.example.go4lunch24kotlin.viewModel

import android.app.Application
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.Restaurant
import com.example.go4lunch24kotlin.models.RestaurantsViewState
import com.example.go4lunch24kotlin.models.RestaurantsWrapperViewState
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.models.poko.OpeningHours
import com.example.go4lunch24kotlin.models.poko.Photo
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.UserSearchRepository
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.useCase.GetNearbySearchResultsUseCase
import com.example.go4lunch24kotlin.useCase.GetRestaurantDetailsResultsUseCase
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Collections
import java.util.Comparator
import java.util.Locale
import kotlin.math.roundToInt

class RestaurantsViewModel(
    private val application: Application,
    locationRepository: LocationRepository,
    getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase,
    getRestaurantDetailsResultsUseCase: GetRestaurantDetailsResultsUseCase,
    workMateRestaurantChoice: WorkMatesChoiceRepository,
    userSearchRepository: UserSearchRepository,
    private val clock: Clock
) : ViewModel() {

    companion object {
        private const val TAG = "RestaurantsViewModel"
    }

    private var locationLiveData: MutableLiveData<Location?> = locationRepository.getLocationLiveData()
    private var nearbySearchResultsLiveData: LiveData<NearbySearchResults> = getNearbySearchResultsUseCase.invoke()
    private var restaurantsDetailsResultLiveData: LiveData<List<RestaurantDetailsResult>> = getRestaurantDetailsResultsUseCase.invoke()
    private var workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<WorkMateRestaurantChoice>> = workMateRestaurantChoice.getWorkmatesRestaurantChoice()
    private var usersSearchLiveData: LiveData<String> = userSearchRepository.getUsersSearchLiveData()

    val getRestaurantsWrapperViewStateMediatorLiveData =
        MediatorLiveData<RestaurantsWrapperViewState>().apply {
            addSource(locationLiveData) { location ->
                Log.d(TAG, "Location data source changed")

                combine(
                    location,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
                Log.d(TAG, "Nearby search results data source changed")

                combine(
                    locationLiveData.value,
                    nearbySearchResults,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(restaurantsDetailsResultLiveData) { restaurantDetailsResults ->
                Log.d(TAG, "Restaurant details results data source changed")

                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantDetailsResults,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
                Log.d(TAG, "Workmates choices data source changed")

                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    userWithFavoriteRestaurants,
                    usersSearchLiveData.value
                )
            }
            addSource(usersSearchLiveData) { usersSearch ->
                Log.d(TAG, "User search query data source changed")

                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearch
                )
            }
        }

    private fun combine(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        restaurantDetailsResults: List<RestaurantDetailsResult>?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>?,
        usersSearch: String?
    ) {
        Log.d(TAG, "Combining data sources for updated view state")


        if (location != null && workMateRestaurantChoice != null) {
            if (nearbySearchResults != null && restaurantDetailsResults != null && !usersSearch.isNullOrEmpty()) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapUsersSearch(
                    nearbySearchResults,
                    restaurantDetailsResults,
                    location,
                    workMateRestaurantChoice,
                    usersSearch
                )
            } else if (restaurantDetailsResults == null && nearbySearchResults != null) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapWithoutDetails(
                    location,
                    nearbySearchResults,
                    workMateRestaurantChoice
                )
            } else if (restaurantDetailsResults != null && nearbySearchResults != null) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapWithDetails(
                    location,
                    nearbySearchResults,
                    restaurantDetailsResults,
                    workMateRestaurantChoice
                )
            }
        }
    }

    //************************************************************************//
    ///////////////////////////     DATA MAPPING     ///////////////////////////
    //************************************************************************//

    // 1.USER SEARCH RESULT, EXPOSE ONLY ONE RESULT
    private fun mapUsersSearch(
        nearbySearchResults: NearbySearchResults,
        restaurantDetailsResults: List<RestaurantDetailsResult>,
        location: Location,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>,
        usersSearch: String
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (i in 0 until nearbySearchResults.results!!.size) {

            if (nearbySearchResults.results!![i].restaurantName!!.contains(usersSearch)) {

                val distanceInt = distance(location, nearbySearchResults.results!![i])
                val name = nearbySearchResults.results!![i].restaurantName
                val address = nearbySearchResults.results!![i].restaurantAddress
                val photo = nearbySearchResults.results!![i].restaurantPhotos?.let { photoList ->
                    photoReference(
                        photoList
                    )
                }
                val distance: String = distanceInt.toString() + application.getString(R.string.m)
                val openingHours = getOpeningHours(
                    restaurantDetailsResults[i].result?.openingHours,
                    nearbySearchResults.results!![i].isPermanentlyClosed
                )
                val rating: Double = convertRatingStars(nearbySearchResults.results!![i].rating)
                val restaurantId = nearbySearchResults.results!![i].placeId
                val usersWhoChoseThisRestaurant: String =
                    usersWhoChoseThisRestaurant(restaurantId, workMateRestaurantChoice)
                val textColor: Int = getTextColor(openingHours)

                restaurantList.add(
                    RestaurantsViewState(
                        distanceInt,
                        name,
                        address,
                        photo,
                        distance,
                        openingHours,
                        rating,
                        restaurantId,
                        usersWhoChoseThisRestaurant,
                        textColor
                    )
                )
            }
        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    // 2.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE CAUSE TO CONNECTION PROBLEM)
    private fun mapWithoutDetails(
        location: Location,
        nearbySearchResults: NearbySearchResults,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (i in 1 until nearbySearchResults.results!!.size) {

            val distanceInt = distance(location, nearbySearchResults.results!![i])
            val name = nearbySearchResults.results!![i].restaurantName
            val address = nearbySearchResults.results!![i].restaurantAddress
            val photo = nearbySearchResults.results!![i].restaurantPhotos?.let { photoList ->
                photoReference(
                    photoList
                )
            }
            val distance: String = distanceInt.toString() + application.getString(R.string.m)
            val openingHours =
                getOpeningHoursWithoutDetails(nearbySearchResults.results!![i].openingHours)
            val rating: Double = convertRatingStars(nearbySearchResults.results!![i].rating)
            val restaurantId = nearbySearchResults.results!![i].placeId
            val usersWhoChoseThisRestaurant: String =
                usersWhoChoseThisRestaurant(restaurantId, workMateRestaurantChoice)
            val textColor: Int = getTextColor(openingHours)

            restaurantList.add(
                RestaurantsViewState(
                    distanceInt,
                    name,
                    address,
                    photo,
                    distance,
                    openingHours,
                    rating,
                    restaurantId,
                    usersWhoChoseThisRestaurant,
                    textColor
                )
            )
        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    // 3.NEARBY SEARCH WITH PLACE DETAILS DATA
    private fun mapWithDetails(
        location: Location,
        nearbySearchResults: NearbySearchResults,
        restaurantDetailsResults: List<RestaurantDetailsResult>,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (restaurant in nearbySearchResults.results!!) {
            for (i in restaurantDetailsResults.indices) {
                if (restaurantDetailsResults[i].result!!.placeId.equals(restaurant.placeId)) {

                    val distanceInt = distance(location, restaurant)
                    val name = restaurant.restaurantName
                    val address = restaurant.restaurantAddress
                    val photo = restaurant.restaurantPhotos?.let { photoList ->
                        photoReference(
                            photoList
                        )
                    }
                    val distance: String = distanceInt.toString() + application.getString(R.string.m)
                    val openingHours =
                        getOpeningHours(restaurantDetailsResults[i].result!!.openingHours, restaurant.isPermanentlyClosed)
                    val rating: Double = convertRatingStars(restaurant.rating)
                    val restaurantId = restaurant.placeId
                    val usersWhoChoseThisRestaurant: String =
                        usersWhoChoseThisRestaurant(restaurantId, workMateRestaurantChoice)
                    val textColor: Int = getTextColor(openingHours)

                    restaurantList.add(
                        RestaurantsViewState(
                            distanceInt,
                            name,
                            address,
                            photo,
                            distance,
                            openingHours,
                            rating,
                            restaurantId,
                            usersWhoChoseThisRestaurant,
                            textColor
                        )
                    )
                }
            }

        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    private fun distance(location: Location, restaurant: Restaurant): Int {

        val results = FloatArray(1)
        val restaurantLat = restaurant.restaurantGeometry!!.restaurantLatLngLiteral!!.lat
        val restaurantLng = restaurant.restaurantGeometry!!.restaurantLatLngLiteral!!.lng

        when {
            restaurantLat != null && restaurantLng != null -> {
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    restaurantLat,
                    restaurantLng,
                    results
                )
            }
        }
        return results[0].toInt()

    }

    private fun photoReference(photoList: List<Photo>?): String {

        var result: String? = null

        if (photoList != null) {
            for (photo in photoList) when {
                photo.photoReference?.isNotEmpty() == true -> {
                    result = (application.getString(R.string.api_url)
                            + application.getString(R.string.photo_reference)
                            + photo.photoReference
                            + application.getString(R.string.and_key)
                            + application.getString(R.string.MAPS_API_KEY))
                }
            }
        }
        return result!!
    }

    private fun getOpeningHours(
        openingHours: OpeningHours?,
        permanentlyClosedStatus: Boolean
    ): String {

        // DEFAULT MESSAGE IF NO OPENING HOUR DATA
        var messageToDisplay = application.getString(R.string.opening_hours_unavailable)

        if (openingHours != null) {
            val currentLocalDate = LocalDateTime.now(clock)
            // IF THE PERIOD LIST HAS ONLY ONE ELEMENT, CONSIDER THAT THE PLACE IS A 24/7 ONE
            if (openingHours.periods != null) {
                if (openingHours.periods!!.size == 1) {
                    if (openingHours.openNow!!) {
                        messageToDisplay = application.getString(R.string.open_h24)
                    }
                } else if (openingHours.periods!!.size > 1) {
                    var selectedOpeningDateTime: LocalDateTime? = null
                    var selectedClosingDateTime: LocalDateTime? = null

                    // SET OPENING AND CLOSED HOURS IF AFTER CURRENT DATE, TAKE THE CLOSEST MATCH
                    for (period in openingHours.periods!!) {

                        val openingHourToConsider: LocalDateTime = convertOpeningHours(
                            period.open!!.time!!,
                            period.open!!.day!!
                        )
                        val closingHourToConsider: LocalDateTime = convertOpeningHours(
                            period.close!!.time!!,
                            period.close!!.day!!
                        )
                        if (openingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(
                                selectedOpeningDateTime,
                                openingHourToConsider
                            )
                        ) {
                            selectedOpeningDateTime = openingHourToConsider
                        }
                        if (closingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(
                                selectedClosingDateTime,
                                closingHourToConsider
                            )
                        ) {
                            selectedClosingDateTime = closingHourToConsider
                        }
                    }
                    if (selectedOpeningDateTime != null) {
                        if (selectedOpeningDateTime.isAfter(selectedClosingDateTime)) {
                            val closingSoonDate =
                                selectedClosingDateTime!!.minus(1, ChronoUnit.HOURS)
                            messageToDisplay =
                                application.getString(R.string.open_until) + getReadableHour(
                                    selectedClosingDateTime
                                )

                            if (currentLocalDate.isAfter(closingSoonDate)) {
                                messageToDisplay = application.getString(R.string.closing_soon)
                            }
                        } else if (selectedOpeningDateTime.isAfter(currentLocalDate)) {
                            messageToDisplay = application.getString(R.string.closed_until) +
                                    getReadableDay(selectedOpeningDateTime) +
                                    getReadableHour(selectedOpeningDateTime)
                        }
                    }
                }// IF THE PERIOD LIST IS EMPTY, RETRIEVE ONLY OPEN STATUS
            } else if (!openingHours.openNow!!) {
                messageToDisplay = application.getString(R.string.closed)
            } else if (openingHours.openNow!!) {
                messageToDisplay = application.getString(R.string.open)
            }
        }
        if (permanentlyClosedStatus) {
            messageToDisplay = application.getString(R.string.permanently_closed)
        }
        return messageToDisplay
    }

    private fun convertOpeningHours(time: String, day: Int): LocalDateTime {

        val hour = time.substring(0, 2)
        val minutes = time.substring(2, 4)
        val hourInt = hour.toInt()
        val minuteInt = minutes.toInt()
        var dayToAdd: Int = day - getCurrentNumericDay()

        when {
            dayToAdd < 0 -> {
                dayToAdd += 7
            }
        }

        var dayOfMonth: Int = getCurrentDayOfMonth() + dayToAdd
        var month: Int = currentMonth()
        var year: Int = currentYear()

        when {
            dayOfMonth > 30 && isEvenMonth() -> {
                dayOfMonth -= 30
                month += 1
            }
        }
        when {
            dayOfMonth > 31 -> {
                dayOfMonth -= 31
                month += 1
            }
        }
        when {
            month > 12 -> {
                month = 1
                year += 1
            }
        }
        return LocalDateTime.of(year, month, dayOfMonth, hourInt, minuteInt)

    }

    private fun getOpeningHoursWithoutDetails(openingHours: OpeningHours?): String {

        val openStatus: String = if (openingHours != null) {
            when {
                openingHours.openNow!! -> {
                    application.getString(R.string.open)
                }
                else -> {
                    application.getString(R.string.closed)
                }
            }
        } else {
            application.getString(R.string.opening_hours_unavailable)
        }
        return openStatus
    }

    private fun getCurrentNumericDay(): Int {
        val currentDate = LocalDateTime.now(clock)
        var dayOfWeek = currentDate.dayOfWeek.value
        // CONVERT SUNDAY TO 0 (NEEDED TO GET SAME DAY AS OPENING HOURS)
        when (dayOfWeek) {
            7 -> {
                dayOfWeek = 0
            }
        }
        return dayOfWeek
    }

    private fun currentYear(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.year
    }

    private fun currentMonth(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.monthValue
    }

    private fun getCurrentDayOfMonth(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.dayOfMonth
    }

    // RETURN TRUE IF 30 DAYS MONTH
    private fun isEvenMonth(): Boolean {
        return currentMonth() == 2
                || currentMonth() == 4
                || currentMonth() == 6
                || currentMonth() == 9
                || currentMonth() == 11
    }

    private fun isConsiderClosestThanSelected(
        selectedHour: LocalDateTime?,
        hourToConsider: LocalDateTime
    ): Boolean {
        return when (selectedHour) {
            null -> {
                true
            }
            else -> hourToConsider.isBefore(selectedHour)
        }
    }

    private fun getReadableHour(selectedHour: LocalDateTime): String {

        val minReadable: String
        val meridian: String
        var hour: Int = selectedHour.hour
        val minutes: Int = selectedHour.minute

        when {
            hour > 12 -> {
                hour -= 12
                meridian = application.getString(R.string.pm)
            }
            else -> {
                meridian = application.getString(R.string.am)
            }
        }
        minReadable = when {
            minutes == 0 -> {
                application.getString(R.string.dot)
            }
            minutes < 10 -> {
                application.getString(R.string.two_dots_for_minutes) + minutes
            }
            else -> {
                application.getString(R.string.two_dots) + minutes
            }
        }
        return " $hour$minReadable$meridian"
    }

    private fun getReadableDay(selectedOpeningDateTime: LocalDateTime): String {

        return when {
            selectedOpeningDateTime.dayOfWeek != LocalDateTime.now(clock).dayOfWeek -> {
                val str: String =
                    when (selectedOpeningDateTime.dayOfWeek.value) {
                        LocalDateTime.now(clock).dayOfWeek.value + 1 -> {
                            application.getString(R.string.tomorrow)
                        }
                        else -> {
                            selectedOpeningDateTime.dayOfWeek.toString().lowercase(Locale.ROOT)
                        }
                    }
                " " + str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
            }
            else -> {
                ""
            }
        }
    }

    private fun convertRatingStars(rating: Double): Double {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return (rating * 3 / 5).roundToInt().toDouble()
    }

    private fun usersWhoChoseThisRestaurant(
        restaurantId: String?,
        workMateRestaurantChoice: List<WorkMateRestaurantChoice>
    ): String {

        var likes = 0

        for (i in workMateRestaurantChoice.indices) {
            when (workMateRestaurantChoice[i].restaurantId) {
                restaurantId -> {
                    likes += 1
                }
            }
        }
        val likeAsString: String = when {
            likes != 0 -> {
                application.getString(R.string.left_bracket) + likes + application.getString(R.string.right_bracket)
            }
            else -> {
                ""
            }
        }
        return likeAsString
    }

    private fun getTextColor(openingHours: String): Int {

        var textColor = Color.GRAY

        when (openingHours) {
            application.getString(R.string.closing_soon) -> {
                textColor = Color.RED
            }
        }
        return textColor
    }
}