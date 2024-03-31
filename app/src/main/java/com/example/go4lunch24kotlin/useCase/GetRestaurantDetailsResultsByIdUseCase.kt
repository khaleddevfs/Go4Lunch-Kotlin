package com.example.go4lunch24kotlin.useCase

import androidx.lifecycle.LiveData
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import com.example.go4lunch24kotlin.repository.RestaurantDetailsRepository


class GetRestaurantDetailsResultsByIdUseCase(
    private val restaurantDetailsResponseRepository: RestaurantDetailsRepository
) {

    fun invoke(placeId: String): LiveData<RestaurantDetailsResult?> = restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId)
}