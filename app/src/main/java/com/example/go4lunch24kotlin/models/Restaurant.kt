package com.example.go4lunch24kotlin.models

import com.example.go4lunch24kotlin.models.poko.Geometry
import com.example.go4lunch24kotlin.models.poko.OpeningHours
import com.example.go4lunch24kotlin.models.poko.Photo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
data class Restaurant constructor(
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





data class Restaurant(
    @SerializedName("place_id")
    var placeId: String? = null,

    @SerializedName("name")
    var restaurantName: String? = null,

    @SerializedName("vicinity")
    var restaurantAddress: String? = null,

    @SerializedName("photos")
    var restaurantPhotos: List<Photo>? = null,

    @SerializedName("geometry")
    var restaurantGeometry: Geometry? = null,

    @SerializedName("opening_hours")
    var openingHours: OpeningHours? = null,

    @SerializedName("rating")
    var rating: Double = 0.0,

    @SerializedName("user_ratings_total")
    var totalRatings: Int = 0,

    @SerializedName("permanently_closed")
    var isPermanentlyClosed: Boolean = false,

    @SerializedName("formatted_phone_number")
    var formattedPhoneNumber: String? = null,

    @SerializedName("website")
    var website: String? = null
)
 */
data class Restaurant constructor(
    @SerializedName("place_id")
    var restaurantId: String? = null,

    @SerializedName("name")
    var restaurantName: String? = null,

    @SerializedName("vicinity")
    var restaurantAddress: String? = null,

    // Cette ligne semble être redondante et doit être corrigée ou supprimée
    // @SerializedName("vicinity")
    // var placeId: String? = null,

    @SerializedName("photos")
    var restaurantPhotos: List<Photo>? = null,

    @SerializedName("geometry")
    var restaurantGeometry: Geometry? = null,

    @SerializedName("opening_hours")
    var openingHours: OpeningHours? = null,

    @SerializedName("rating")
    var rating: Double = 0.0,

    @SerializedName("user_ratings_total")
    var totalRatings: Int = 0,

    @SerializedName("permanently_closed")
    var isPermanentlyClosed: Boolean = false,

    @SerializedName("formatted_phone_number")
    var formattedPhoneNumber: String? = null,

    @SerializedName("website")
    var website: String? = null
)
