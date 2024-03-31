package com.example.go4lunch24kotlin.models

import androidx.annotation.ColorRes

data class RestaurantsViewState constructor(
    val distanceInt: Int = 0,
    val name: String? = null,
    val address: String? = null,
    val photo: String? = null,
    val distanceText: String? = null,
    val openingHours: String? = null,
    val rating: Double = 0.0,
    val placeId: String? = null,
    val usersWhoChoseThisRestaurant: String? = null,
    @ColorRes
    val textColor: Int = 0
)
