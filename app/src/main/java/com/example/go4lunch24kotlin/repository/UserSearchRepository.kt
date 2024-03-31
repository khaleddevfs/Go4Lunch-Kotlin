package com.example.go4lunch24kotlin.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserSearchRepository {
    private val searchViewResultLiveData = MutableLiveData<String>()

    fun usersSearch(restaurantId: String) {
        searchViewResultLiveData.value = restaurantId
    }

    fun getUsersSearchLiveData(): LiveData<String> {
        return searchViewResultLiveData

        // Retourne temporairement une valeur non null pour tester
       // return searchViewResultLiveData.apply { value = "Test" }
    }
}