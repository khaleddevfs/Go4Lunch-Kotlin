package com.example.go4lunch24kotlin.viewModel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.MainActivityYourLunchViewState
import com.example.go4lunch24kotlin.models.PredictionViewState
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.poko.Predictions
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.UserSearchRepository
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.useCase.GetCurrentUserIdUseCase
import com.example.go4lunch24kotlin.useCase.GetPredictionsUseCase
import com.example.go4lunch24kotlin.util.PermissionsAction
import com.example.go4lunch24kotlin.util.SingleLiveEvent

class MainActivityViewModel constructor(
    private val application: Application,
    private val locationRepository: LocationRepository,
    private val getPredictionsUseCase: GetPredictionsUseCase,
    private val userSearchRepository: UserSearchRepository,
    private val workMatesChoiceRepository: WorkMatesChoiceRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {


    val actionSingleLiveEvent: SingleLiveEvent<PermissionsAction> = SingleLiveEvent()
    val predictionsMediatorLiveData = MediatorLiveData<List<PredictionViewState>>()
    val mainActivityYourLunchViewStateMediatorLiveData = MediatorLiveData<MainActivityYourLunchViewState>()

    // CHECK PERMISSIONS
    fun checkPermission(activity: Activity?) {
        when {
            ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                actionSingleLiveEvent.setValue(PermissionsAction.PERMISSION_DENIED)
            }
            else -> {
                actionSingleLiveEvent.setValue(PermissionsAction.PERMISSION_ASKED)
            }
        }
    }

    // WHEN PERMISSION IS GRANTED, RETRIEVE USER LOCATION
    private fun permissionGranted() {
        locationRepository.startLocationRequest()
    }

    // WHEN CLICKING ON SEARCH VIEW WE PASSED THE TEXT TO USE CASE AND THEN OBSERVE IT
    fun sendTextToAutocomplete(text: String?) {
        val predictionsLiveData: LiveData<Predictions> = getPredictionsUseCase.invoke(text)
        predictionsMediatorLiveData.addSource(
            predictionsLiveData
        ) { predictions: Predictions? ->
            this.combine(
                predictions
            )
        }
    }

    private fun combine(predictions: Predictions?) {
        if (predictions != null) {
            predictionsMediatorLiveData.value = map(predictions)
        }
    }

    // MAP THE PREDICTIONS RESULT TO VIEW STATE
    private fun map(predictions: Predictions): List<PredictionViewState> {
        val predictionsList: MutableList<PredictionViewState> = ArrayList()
        for ((description, structuredFormatting, placeId) in predictions.predictions!!) {
            predictionsList.add(
                PredictionViewState(
                    description!!,
                    placeId!!,
                    structuredFormatting!!.name!!
                )
            )
        }
        return predictionsList
    }

    // RETRIEVE THE CURRENT USER RESTAURANT CHOICE
    fun getUserRestaurantChoice() {
        val workmatesChoiceLiveData: LiveData<List<WorkMateRestaurantChoice>> =
            workMatesChoiceRepository.getWorkmatesRestaurantChoice()
        mainActivityYourLunchViewStateMediatorLiveData.addSource(
            workmatesChoiceLiveData
        ) { workmateChoice ->
            mapUserRestaurantChoice(workmateChoice)
        }
    }

    private fun mapUserRestaurantChoice(userWhoMadeRestaurantChoices: List<WorkMateRestaurantChoice?>) {
        val currentUserId: String = getCurrentUserIdUseCase.invoke()
        var yourLunch = MainActivityYourLunchViewState(
            application.getString(R.string.no_current_user_restaurant_choice),
            0
        )
        for (workmate in userWhoMadeRestaurantChoices) {
            if (workmate != null) {
                if (workmate.userId == currentUserId) {
                    yourLunch = MainActivityYourLunchViewState(
                        workmate.restaurantId,
                        1
                    )
                }
            }
        }
        mainActivityYourLunchViewStateMediatorLiveData.value = yourLunch
    }

    fun userSearch(predictionText: String) {
        userSearchRepository.usersSearch(predictionText)
    }
}