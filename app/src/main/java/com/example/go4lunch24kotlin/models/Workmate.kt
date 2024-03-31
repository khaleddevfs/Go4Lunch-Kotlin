package com.example.go4lunch24kotlin.models
/*
data class Workmate constructor(

    var uid: String? = null,
    var userName: String? = null,
    var avatarURL: String? = null,
    var email: String? = null

)

 */




data class Workmate constructor(
    var uid: String? = null,
    var userName: String? = null,
    var avatarURL: String? = null,
    var email: String? = null,
    var likedRestaurants: MutableList<String> = mutableListOf(),
    var workMateRestaurantChoice: WorkMateRestaurantChoice? = null
) {
    fun addLikedRestaurant(restaurantId: String) {
        if (!likedRestaurants.contains(restaurantId)) {
            likedRestaurants.add(restaurantId)
        }
    }

    fun removeLikedRestaurant(restaurantId: String) {
        likedRestaurants.remove(restaurantId)
    }
}

