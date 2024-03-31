package com.example.go4lunch24kotlin.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.WorkMateViewState
import com.example.go4lunch24kotlin.models.Workmate
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.repository.WorkMatesRepository
import java.lang.Boolean

class WorkMatesViewModel constructor(
    private val application: Application,
    workmatesRepository: WorkMatesRepository,
    workMatesChoiceRepository: WorkMatesChoiceRepository,
) : ViewModel() {

    // HERE WE HAVE 2 COLLECTIONS TO OBSERVE:
    // ONE WITH ALL REGISTERED USERS
    // AND ONE WITH USERS (WORKMATES) WHO MADE A CHOICE
    private var workMatesLiveData: LiveData<List<Workmate>> = workmatesRepository.getWorkmates()
    private var workmatesWhoMadeChoiceLiveData: LiveData<List<WorkMateRestaurantChoice>> =
        workMatesChoiceRepository.getWorkmatesRestaurantChoice()

    val workMatesViewStateMediatorLiveData =
        MediatorLiveData<List<WorkMateViewState>>().apply {
            addSource(workMatesLiveData) { workmates ->
                combine(
                    workmates,
                    workmatesWhoMadeChoiceLiveData.value
                )
            }
            addSource(workmatesWhoMadeChoiceLiveData) { workmatesWhoMadeRestaurantChoices ->
                combine(
                    workMatesLiveData.value,
                    workmatesWhoMadeRestaurantChoices
                )
            }
        }

    // COMBINE THE 2 SOURCES
    private fun combine(
        workmates: List<Workmate>?,
        workmatesWhoMadeRestaurantChoices: List<WorkMateRestaurantChoice>?,
    ) {
        workMatesViewStateMediatorLiveData.value = mapWorkmates(
            workmates,
            workmatesWhoMadeRestaurantChoices
        )
    }

    // MAP TO WORKMATE VIEW STATE
    @SuppressLint("ResourceType")
    private fun mapWorkmates(
        workmates: List<Workmate>?,
        workmatesWhoMadeRestaurantChoices: List<WorkMateRestaurantChoice>?
    ): List<WorkMateViewState> {

        val workMateListViewState = mutableListOf<WorkMateViewState>()

        if (workmates != null) {
            for (workmate in workmates) {
                val workmateName = workmate.userName
                val avatar = workmate.avatarURL
                val workmateId = workmate.uid
                val workmateChoice: String =
                    workmateChoice(workmateId!!, workmatesWhoMadeRestaurantChoices)
                var gotRestaurant = false
                var colorText = Color.GRAY
                if (workmateChoice != " " + application.getString(R.string.has_not_decided_yet)) {
                    colorText = Color.BLACK
                    gotRestaurant = true
                }

                workMateListViewState.add(
                    WorkMateViewState(
                        workmateName = workmateName,
                        workmateDescription = workmateName + workmateChoice,
                        workmatePhoto = avatar,
                        workmateId = workmateId,
                        gotRestaurant = gotRestaurant,
                        textColor = colorText
                    )
                )
            }
        }
        // SORT THE LIST BY BOOLEAN, IF TRUE, APPEARS AT THE TOP OF THE LIST
        workMateListViewState.sortWith { (_, _, _, _, gotRestaurant), (_, _, _, _, gotRestaurant1) ->
            Boolean.compare(
                !gotRestaurant,
                !gotRestaurant1
            )
        }
        return workMateListViewState
    }

    private fun workmateChoice(
        workmateId: String,
        workmatesWhoMadeRestaurantChoices: List<WorkMateRestaurantChoice>?
    ): String {

        var restaurantName = " " + application.getString(R.string.has_not_decided_yet)

        workmatesWhoMadeRestaurantChoices?.let { workmates ->
            workmates.forEach { workmate ->
                if (workmate.userId.equals(workmateId)) {
                    restaurantName = " " + application.getString(R.string.left_bracket) + workmate.restaurantName + application.getString(R.string.right_bracket)
                }
            }
        }
        return restaurantName
    }
}